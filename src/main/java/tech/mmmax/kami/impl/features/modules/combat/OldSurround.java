package tech.mmmax.kami.impl.features.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.PostPacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.utils.player.InventoryUtils;
import tech.mmmax.kami.api.utils.world.BlockUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class OldSurround extends Module {

    Timer timeSystem = new Timer();
    Timer disableTimer = new Timer();
    Value ecThreshold = (new ValueBuilder()).withDescriptor("EChest Threshold").withValue(Double.valueOf(0.8D)).withRange(Double.valueOf(0.0D), Double.valueOf(1.0D)).register(this);
    Value trigger = (new ValueBuilder()).withDescriptor("Trigger").withValue(Boolean.valueOf(true)).register(this);
    Value disableJump = (new ValueBuilder()).withDescriptor("Disable on Jump").withValue(Boolean.valueOf(true)).register(this);
    Value center = (new ValueBuilder()).withDescriptor("Center").withValue(Boolean.valueOf(true)).register(this);
    Value delay = (new ValueBuilder()).withDescriptor("Delay").withValue(Integer.valueOf(3)).withRange(Integer.valueOf(0), Integer.valueOf(200)).withAction((s) -> {
        this.timeSystem.setDelay(((Number) s.getValue()).longValue());
    }).register(this);
    Value triggerDelay = (new ValueBuilder()).withDescriptor("Trigger Delay").withValue(Integer.valueOf(3)).withRange(Integer.valueOf(1), Integer.valueOf(2000)).withAction((s) -> {
        this.disableTimer.setDelay(((Number) s.getValue()).longValue());
    }).register(this);
    Value blocksPerTick = (new ValueBuilder()).withDescriptor("Blocks Per Tick").withValue(Integer.valueOf(2)).withRange(Integer.valueOf(1), Integer.valueOf(10)).register(this);
    Value floor = (new ValueBuilder()).withDescriptor("Floor").withValue(Boolean.valueOf(false)).register(this);
    Value dynamic = (new ValueBuilder()).withDescriptor("Dynamic").withValue(Boolean.valueOf(false)).register(this);
    Value sneak = (new ValueBuilder()).withDescriptor("Sneak").withValue(Boolean.valueOf(true)).register(this);
    Value antiCity = (new ValueBuilder()).withDescriptor("Anti City").withValue(Boolean.valueOf(false)).register(this);
    Value offhandSwap = (new ValueBuilder()).withDescriptor("Offhand Swap").withValue(Boolean.valueOf(false)).register(this);
    List placedBlocks = new ArrayList();
    BlockPos[] offsets = new BlockPos[] { new BlockPos(0, -1, 0), new BlockPos(1, -1, 0), new BlockPos(0, -1, 1), new BlockPos(-1, -1, 0), new BlockPos(0, -1, -1), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1)};

    public OldSurround() {
        super("OldSurround", Feature.Category.Combat);
    }

    public void onEnable() {
        super.onEnable();
        if (!NullUtils.nullCheck()) {
            this.timeSystem.resetDelay();
            if (((Boolean) this.center.getValue()).booleanValue()) {
                OldSurround.mc.player.connection.sendPacket(new Position((double) this.getCenterOfBlock().getX() + 0.5D, OldSurround.mc.player.posY, (double) this.getCenterOfBlock().getZ() + 0.5D, OldSurround.mc.player.onGround));
                OldSurround.mc.player.setPosition((double) this.getCenterOfBlock().getX() + 0.5D, OldSurround.mc.player.posY, (double) this.getCenterOfBlock().getZ() + 0.5D);
            }

            this.placedBlocks.clear();
            this.disableTimer.resetDelay();
        }
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            int oldSlot = OldSurround.mc.player.inventory.currentItem;
            Item offhandItem = OldSurround.mc.player.getHeldItemOffhand().getItem();

            if (InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN)) == -1) {
                this.setEnabled(false);
            } else if (!OldSurround.mc.player.onGround && ((Boolean) this.disableJump.getValue()).booleanValue()) {
                this.setEnabled(false);
            } else {
                if (this.timeSystem.isPassed()) {
                    int blocksInTick = 0;
                    int hotbarSlot = InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN));

                    if (hotbarSlot == -1) {
                        return;
                    }

                    BlockPos[] offsetArray = ((Boolean) this.dynamic.getValue()).booleanValue() ? this.getDynamicOffsets() : this.addToPlayer(this.offsets);
                    List verifiedPositions = this.getPlacableSurrounds(offsetArray);

                    if (verifiedPositions.isEmpty()) {
                        return;
                    }

                    if (((Boolean) this.sneak.getValue()).booleanValue()) {
                        OldSurround.mc.getConnection().sendPacket(new CPacketEntityAction(OldSurround.mc.player, Action.START_SNEAKING));
                    }

                    if (((Boolean) this.offhandSwap.getValue()).booleanValue()) {
                        InventoryUtils.moveItemToOffhand(Item.getItemFromBlock(Blocks.OBSIDIAN));
                    } else {
                        InventoryUtils.switchToSlotGhost(hotbarSlot);
                    }

                    Iterator iterator = verifiedPositions.iterator();

                    while (iterator.hasNext()) {
                        BlockPos pos = (BlockPos) iterator.next();

                        if (pos == null) {
                            break;
                        }

                        if (OldSurround.mc.world.getBlockState(pos).getMaterial().isReplaceable() && BlockUtils.placeBlock(pos, false, ((Boolean) this.offhandSwap.getValue()).booleanValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND)) {
                            ++blocksInTick;
                            this.placedBlocks.add(pos);
                            if (blocksInTick > ((Number) this.blocksPerTick.getValue()).intValue()) {
                                break;
                            }
                        }
                    }

                    if (((Boolean) this.offhandSwap.getValue()).booleanValue()) {
                        InventoryUtils.moveItemToOffhand(offhandItem);
                    } else {
                        InventoryUtils.switchToSlotGhost(oldSlot);
                    }

                    this.timeSystem.resetDelay();
                    if (((Boolean) this.sneak.getValue()).booleanValue()) {
                        OldSurround.mc.getConnection().sendPacket(new CPacketEntityAction(OldSurround.mc.player, Action.STOP_SNEAKING));
                    }
                }

                if (this.disableTimer.isPassed() && ((Boolean) this.trigger.getValue()).booleanValue()) {
                    this.setEnabled(false);
                }

            }
        }
    }

    @SubscribeEvent
    public void onPostPacket(PostPacketEvent event) {
        if (!NullUtils.nullCheck()) {
            if (event.getPacket() instanceof SPacketBlockChange && ((Boolean) this.antiCity.getValue()).booleanValue()) {
                SPacketBlockChange packet = (SPacketBlockChange) event.getPacket();

                if (packet.getBlockState().getBlock() == Blocks.AIR && OldSurround.mc.player.getDistance((double) packet.getBlockPosition().getX(), (double) packet.getBlockPosition().getY(), (double) packet.getBlockPosition().getZ()) <= 2.0D) {
                    this.onUpdate((ClientTickEvent) null);
                }
            }

        }
    }

    List getPlacableSurrounds(BlockPos[] offsets) {
        ArrayList allowed = new ArrayList();
        BlockPos[] ablockpos = offsets;
        int i = offsets.length;

        for (int j = 0; j < i; ++j) {
            BlockPos pos = ablockpos[j];

            if (pos != null && OldSurround.mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
                boolean allow = true;
                Iterator iterator = OldSurround.mc.world.getEntitiesWithinAABBExcludingEntity((Entity) null, new AxisAlignedBB(pos)).iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    if (entity instanceof EntityPlayer) {
                        allow = false;
                        break;
                    }
                }

                if (allow) {
                    allowed.add(pos);
                }
            }
        }

        return allowed;
    }

    @SubscribeEvent
    public void onJump(LivingJumpEvent e) {
        if (e.getEntity().equals(OldSurround.mc.player)) {
            if (((Boolean) this.disableJump.getValue()).booleanValue()) {
                this.setEnabled(false);
            }
        } else if (e.getEntity() == OldSurround.mc.player && ((Boolean) this.disableJump.getValue()).booleanValue()) {
            this.setEnabled(false);
        }

    }

    BlockPos[] getDynamicOffsets() {
        BlockPos[] positions = new BlockPos[1000];
        BlockPos playerPos = this.getPlayerPos();
        double borderMax = 0.7D;
        double borderMin = 0.3D;
        double decimalX = OldSurround.mc.player.posX - Math.floor(OldSurround.mc.player.posX);
        double decimalZ = OldSurround.mc.player.posZ - Math.floor(OldSurround.mc.player.posZ);
        int xOffset = decimalX >= borderMax ? 1 : (decimalX <= borderMin ? -1 : 0);
        int zOffset = decimalZ >= borderMax ? 1 : (decimalZ <= borderMin ? -1 : 0);
        int i = 0;
        EnumFacing[] aenumfacing = EnumFacing.HORIZONTALS;
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing facing = aenumfacing[j];

            if (facing == EnumFacing.EAST) {
                positions[i] = playerPos.add(decimalX >= borderMax ? facing.getXOffset() : 0, -1, 0);
                ++i;
                positions[i] = playerPos.add(decimalX >= borderMax ? facing.getXOffset() * 2 : facing.getXOffset(), -1, 0);
                ++i;
                positions[i] = playerPos.add(decimalX >= borderMax ? facing.getXOffset() * 2 : facing.getXOffset(), 0, 0);
                ++i;
                if (zOffset != 0) {
                    positions[i] = playerPos.add(decimalX >= borderMax ? facing.getXOffset() : 0, -1, zOffset);
                    ++i;
                    positions[i] = playerPos.add(decimalX >= borderMax ? facing.getXOffset() * 2 : facing.getXOffset(), -1, zOffset);
                    ++i;
                    positions[i] = playerPos.add(decimalX >= borderMax ? facing.getXOffset() * 2 : facing.getXOffset(), 0, zOffset);
                    ++i;
                }
            }

            if (facing == EnumFacing.WEST) {
                positions[i] = playerPos.add(decimalX <= borderMin ? facing.getXOffset() : 0, -1, 0);
                ++i;
                positions[i] = playerPos.add(decimalX <= borderMin ? facing.getXOffset() * 2 : facing.getXOffset(), -1, 0);
                ++i;
                positions[i] = playerPos.add(decimalX <= borderMin ? facing.getXOffset() * 2 : facing.getXOffset(), 0, 0);
                ++i;
                if (zOffset != 0) {
                    positions[i] = playerPos.add(decimalX <= borderMin ? facing.getXOffset() : 0, -1, zOffset);
                    ++i;
                    positions[i] = playerPos.add(decimalX <= borderMin ? facing.getXOffset() * 2 : facing.getXOffset(), -1, zOffset);
                    ++i;
                    positions[i] = playerPos.add(decimalX <= borderMin ? facing.getXOffset() * 2 : facing.getXOffset(), 0, zOffset);
                    ++i;
                }
            }

            if (facing == EnumFacing.NORTH) {
                positions[i] = playerPos.add(0, -1, decimalZ <= borderMin ? facing.getZOffset() : 0);
                ++i;
                positions[i] = playerPos.add(0, -1, decimalZ <= borderMin ? facing.getZOffset() * 2 : facing.getZOffset());
                ++i;
                positions[i] = playerPos.add(0, 0, decimalZ <= borderMin ? facing.getZOffset() * 2 : facing.getZOffset());
                ++i;
                if (xOffset != 0) {
                    positions[i] = playerPos.add(xOffset, -1, decimalZ <= borderMin ? facing.getZOffset() : 0);
                    ++i;
                    positions[i] = playerPos.add(xOffset, -1, decimalZ <= borderMin ? facing.getZOffset() * 2 : facing.getZOffset());
                    ++i;
                    positions[i] = playerPos.add(xOffset, 0, decimalZ <= borderMin ? facing.getZOffset() * 2 : facing.getZOffset());
                    ++i;
                }
            }

            if (facing == EnumFacing.SOUTH) {
                positions[i] = playerPos.add(0, -1, decimalZ >= borderMax ? facing.getZOffset() : 0);
                ++i;
                positions[i] = playerPos.add(0, -1, decimalZ >= borderMax ? facing.getZOffset() * 2 : facing.getZOffset());
                ++i;
                positions[i] = playerPos.add(0, 0, decimalZ >= borderMax ? facing.getZOffset() * 2 : facing.getZOffset());
                ++i;
                if (xOffset != 0) {
                    positions[i] = playerPos.add(xOffset, -1, decimalZ >= borderMax ? facing.getZOffset() : 0);
                    ++i;
                    positions[i] = playerPos.add(xOffset, -1, decimalZ >= borderMax ? facing.getZOffset() * 2 : facing.getZOffset());
                    ++i;
                    positions[i] = playerPos.add(xOffset, 0, decimalZ >= borderMax ? facing.getZOffset() * 2 : facing.getZOffset());
                    ++i;
                }
            }
        }

        return positions;
    }

    BlockPos[] addToPlayer(BlockPos[] offsets) {
        BlockPos[] positions = new BlockPos[1000];
        int i = 0;
        BlockPos[] ablockpos = offsets;
        int i = offsets.length;

        for (int j = 0; j < i; ++j) {
            BlockPos b = ablockpos[j];

            if (b != null) {
                positions[i] = this.getPlayerPos().add(b);
                ++i;
            }
        }

        return positions;
    }

    BlockPos getCenterOfBlock() {
        double newX = Math.floor(OldSurround.mc.player.posX) + 0.5D;
        double newZ = Math.floor(OldSurround.mc.player.posZ) + 0.5D;

        return new BlockPos(newX, OldSurround.mc.player.posY, newZ);
    }

    BlockPos getPlayerPos() {
        double decimalPoint = OldSurround.mc.player.posY - Math.floor(OldSurround.mc.player.posY);

        return new BlockPos(((Boolean) this.floor.getValue()).booleanValue() ? Math.floor(OldSurround.mc.player.posX) : OldSurround.mc.player.posX, decimalPoint > ((Number) this.ecThreshold.getValue()).doubleValue() ? Math.floor(OldSurround.mc.player.posY) + 1.0D : Math.floor(OldSurround.mc.player.posY), ((Boolean) this.floor.getValue()).booleanValue() ? Math.floor(OldSurround.mc.player.posZ) : OldSurround.mc.player.posZ);
    }
}
