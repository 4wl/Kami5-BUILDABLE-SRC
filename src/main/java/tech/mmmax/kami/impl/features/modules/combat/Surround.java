package tech.mmmax.kami.impl.features.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.utils.player.InventoryUtils;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.utils.world.BlockUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class Surround extends Module {

    Timer timer = new Timer();
    Value delay = (new ValueBuilder()).withDescriptor("Delay").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(0), Integer.valueOf(1000)).withAction((set) -> {
        this.timer.setDelay(((Number) set.getValue()).longValue());
    }).register(this);
    Value blocksPerTick = (new ValueBuilder()).withDescriptor("BPT").withValue(Integer.valueOf(20)).withRange(Integer.valueOf(1), Integer.valueOf(50)).register(this);
    Value retryAmount = (new ValueBuilder()).withDescriptor("Retry Amount").withValue(Integer.valueOf(20)).withRange(Integer.valueOf(1), Integer.valueOf(50)).register(this);
    Value dynamic = (new ValueBuilder()).withDescriptor("Dynamic").withValue(Boolean.valueOf(true)).register(this);
    Value antiPhase = (new ValueBuilder()).withDescriptor("Anti Phase").withValue(Boolean.valueOf(true)).register(this);
    Value predict = (new ValueBuilder()).withDescriptor("Predict").withValue(Boolean.valueOf(false)).register(this);
    Value jumpDisable = (new ValueBuilder()).withDescriptor("Jump Disable").withValue(Boolean.valueOf(true)).register(this);
    Value activeFillColor = (new ValueBuilder()).withDescriptor("Active Fill Color").withValue(new Color(0, 200, 12, 20)).register(this);
    Value activeLineColor = (new ValueBuilder()).withDescriptor("Active Line Color").withValue(new Color(0, 200, 12, 255)).register(this);
    double startY = 0.0D;
    List activeBlocks = new ArrayList();
    boolean shouldPredict = false;

    public Surround() {
        super("Surround", Feature.Category.Combat);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            this.shouldPredict = true;
            if (((Boolean) this.jumpDisable.getValue()).booleanValue() && (!Surround.mc.player.onGround || Surround.mc.player.posY != this.startY)) {
                this.setEnabled(false);
            } else {
                if (this.timer.isPassed()) {
                    this.activeBlocks.clear();
                    boolean switched = false;
                    int oldSlot = Surround.mc.player.inventory.currentItem;
                    int blockSlot = this.getSlot();

                    if (blockSlot == -1) {
                        this.setEnabled(false);
                        return;
                    }

                    int blocksInTick = 0;

                    for (int i = 0; i < ((Number) this.retryAmount.getValue()).intValue(); ++i) {
                        Iterator iterator = this.getOffsets().iterator();

                        while (iterator.hasNext()) {
                            BlockPos pos = (BlockPos) iterator.next();

                            if (blocksInTick > ((Number) this.blocksPerTick.getValue()).intValue()) {
                                break;
                            }

                            if (this.canPlaceBlock(pos)) {
                                this.activeBlocks.add(pos);
                                if (!switched) {
                                    InventoryUtils.switchToSlotGhost(blockSlot);
                                    switched = true;
                                }

                                BlockUtils.placeBlock(pos, true);
                                ++blocksInTick;
                            }
                        }
                    }

                    if (switched) {
                        InventoryUtils.switchToSlotGhost(oldSlot);
                    }

                    this.timer.resetDelay();
                }

            }
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketBlockChange && ((Boolean) this.predict.getValue()).booleanValue()) {
            SPacketBlockChange packet = (SPacketBlockChange) event.getPacket();
            Iterator iterator = this.getOffsets().iterator();

            while (iterator.hasNext()) {
                BlockPos pos = (BlockPos) iterator.next();

                if (this.shouldPredict && pos.equals(packet.getBlockPosition()) && packet.getBlockState().getBlock() == Blocks.AIR) {
                    int oldSlot = Surround.mc.player.inventory.currentItem;
                    int blockSlot = this.getSlot();

                    if (blockSlot == -1) {
                        return;
                    }

                    InventoryUtils.switchToSlotGhost(blockSlot);
                    BlockUtils.placeBlock(pos, true);
                    InventoryUtils.switchToSlotGhost(oldSlot);
                    this.shouldPredict = false;
                    break;
                }
            }
        }

    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!NullUtils.nullCheck()) {
            Iterator iterator = this.activeBlocks.iterator();

            while (iterator.hasNext()) {
                BlockPos pos = (BlockPos) iterator.next();

                RenderUtil.renderBB(7, new AxisAlignedBB(pos), (Color) this.activeFillColor.getValue(), (Color) this.activeFillColor.getValue());
                RenderUtil.renderBB(3, new AxisAlignedBB(pos), (Color) this.activeLineColor.getValue(), (Color) this.activeLineColor.getValue());
            }

        }
    }

    public void onEnable() {
        if (!NullUtils.nullCheck()) {
            super.onEnable();
            this.startY = Surround.mc.player.posY;
        }
    }

    int getSlot() {
        boolean slot = true;
        int slot1 = InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN));

        if (slot1 == -1) {
            slot1 = InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock(Blocks.ENDER_CHEST));
        }

        return slot1;
    }

    boolean canPlaceBlock(BlockPos pos) {
        boolean allow = true;

        if (!Surround.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            allow = false;
        }

        Iterator iterator = Surround.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityPlayer) {
                allow = false;
                break;
            }
        }

        return allow;
    }

    List getOffsets() {
        BlockPos playerPos = this.getPlayerPos();
        ArrayList offsets = new ArrayList();

        if (((Boolean) this.dynamic.getValue()).booleanValue()) {
            double decimalX = Math.abs(Surround.mc.player.posX) - Math.floor(Math.abs(Surround.mc.player.posX));
            double decimalZ = Math.abs(Surround.mc.player.posZ) - Math.floor(Math.abs(Surround.mc.player.posZ));

            this.calcOffset(decimalX);
            this.calcOffset(decimalZ);
            int lengthXPos = this.calcLength(decimalX, false);
            int lengthXNeg = this.calcLength(decimalX, true);
            int lengthZPos = this.calcLength(decimalZ, false);
            int lengthZNeg = this.calcLength(decimalZ, true);
            ArrayList tempOffsets = new ArrayList();

            offsets.addAll(this.getOverlapPos());

            int z;

            for (z = 1; z < lengthXPos + 1; ++z) {
                tempOffsets.add(this.addToPlayer(playerPos, (double) z, 0.0D, (double) (1 + lengthZPos)));
                tempOffsets.add(this.addToPlayer(playerPos, (double) z, 0.0D, (double) (-(1 + lengthZNeg))));
            }

            for (z = 0; z <= lengthXNeg; ++z) {
                tempOffsets.add(this.addToPlayer(playerPos, (double) (-z), 0.0D, (double) (1 + lengthZPos)));
                tempOffsets.add(this.addToPlayer(playerPos, (double) (-z), 0.0D, (double) (-(1 + lengthZNeg))));
            }

            for (z = 1; z < lengthZPos + 1; ++z) {
                tempOffsets.add(this.addToPlayer(playerPos, (double) (1 + lengthXPos), 0.0D, (double) z));
                tempOffsets.add(this.addToPlayer(playerPos, (double) (-(1 + lengthXNeg)), 0.0D, (double) z));
            }

            for (z = 0; z <= lengthZNeg; ++z) {
                tempOffsets.add(this.addToPlayer(playerPos, (double) (1 + lengthXPos), 0.0D, (double) (-z)));
                tempOffsets.add(this.addToPlayer(playerPos, (double) (-(1 + lengthXNeg)), 0.0D, (double) (-z)));
            }

            Iterator iterator = tempOffsets.iterator();

            while (iterator.hasNext()) {
                BlockPos pos = (BlockPos) iterator.next();

                offsets.add(pos.add(0, -1, 0));
                offsets.add(pos);
            }
        } else {
            offsets.add(playerPos.add(0, -1, 0));
            EnumFacing[] aenumfacing = EnumFacing.HORIZONTALS;
            int i = aenumfacing.length;

            for (int j = 0; j < i; ++j) {
                EnumFacing facing = aenumfacing[j];

                offsets.add(playerPos.add(facing.getXOffset(), -1, facing.getZOffset()));
                offsets.add(playerPos.add(facing.getXOffset(), 0, facing.getZOffset()));
            }
        }

        return offsets;
    }

    BlockPos addToPlayer(BlockPos playerPos, double x, double y, double z) {
        if (playerPos.getX() < 0) {
            x = -x;
        }

        if (playerPos.getY() < 0) {
            y = -y;
        }

        if (playerPos.getZ() < 0) {
            z = -z;
        }

        return playerPos.add(x, y, z);
    }

    int calcLength(double decimal, boolean negative) {
        return negative ? (decimal <= 0.3D ? 1 : 0) : (decimal >= 0.7D ? 1 : 0);
    }

    boolean isOverlapping(int offsetX, int offsetZ) {
        boolean overlapping = false;
        double decimalX = Surround.mc.player.posX - Math.floor(Surround.mc.player.posX);

        decimalX = Math.abs(decimalX);
        double decimalZ = Surround.mc.player.posZ - Math.floor(Surround.mc.player.posZ);

        decimalZ = Math.abs(decimalZ);
        if (offsetX > 0 && decimalX > 0.7D) {
            overlapping = true;
        }

        if (offsetX < 0 && decimalX < 0.3D) {
            overlapping = true;
        }

        if (offsetZ > 0 && decimalZ >= 0.7D) {
            overlapping = true;
        }

        if (offsetZ < 0 && decimalZ < 0.3D) {
            overlapping = true;
        }

        return overlapping;
    }

    List getOverlapPos() {
        ArrayList positions = new ArrayList();
        double decimalX = Surround.mc.player.posX - Math.floor(Surround.mc.player.posX);
        double decimalZ = Surround.mc.player.posZ - Math.floor(Surround.mc.player.posZ);
        int offX = this.calcOffset(decimalX);
        int offZ = this.calcOffset(decimalZ);

        positions.add(this.getPlayerPos());

        for (int x = 0; x <= Math.abs(offX); ++x) {
            for (int z = 0; z <= Math.abs(offZ); ++z) {
                int properX = x * offX;
                int properZ = z * offZ;

                positions.add(this.getPlayerPos().add(properX, -1, properZ));
            }
        }

        return positions;
    }

    int calcOffset(double dec) {
        return dec >= 0.7D ? 1 : (dec <= 0.3D ? -1 : 0);
    }

    BlockPos getPlayerPos() {
        double decimalPoint = Surround.mc.player.posY - Math.floor(Surround.mc.player.posY);

        return new BlockPos(Surround.mc.player.posX, decimalPoint > 0.8D ? Math.floor(Surround.mc.player.posY) + 1.0D : Math.floor(Surround.mc.player.posY), Surround.mc.player.posZ);
    }
}
