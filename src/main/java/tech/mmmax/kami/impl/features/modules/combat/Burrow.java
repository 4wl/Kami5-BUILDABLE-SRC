package tech.mmmax.kami.impl.features.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.chat.ChatMessage;
import tech.mmmax.kami.api.utils.chat.ChatUtils;
import tech.mmmax.kami.api.utils.player.InventoryUtils;
import tech.mmmax.kami.api.utils.world.BlockUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.mixin.mixins.access.ICPacketPlayer;

public class Burrow extends Module {

    Value offset = (new ValueBuilder()).withDescriptor("Offset").withValue(Integer.valueOf(10)).withRange(Integer.valueOf(-20), Integer.valueOf(20)).register(this);
    Value block = (new ValueBuilder()).withDescriptor("Block").withValue("EnderChest").withModes(new String[] { "EnderChest", "Obby"}).register(this);
    Value mode = (new ValueBuilder()).withDescriptor("Mode").withValue("Spread").withModes(new String[] { "Spread", "Instant", "Intercept"}).register(this);
    Value dynamic = (new ValueBuilder()).withDescriptor("Dynamic").withValue(Boolean.valueOf(false)).register(this);
    List originalPos;
    int tick = 0;
    double[] heights = new double[] { 0.41999998688698D, 0.7531999805212D, 1.00133597911214D, 1.16610926093821D};

    public Burrow() {
        super("Burrow", Feature.Category.Combat);
    }

    public void onEnable() {
        super.onEnable();
        if (!NullUtils.nullCheck()) {
            this.originalPos = new ArrayList();
            if (((Boolean) this.dynamic.getValue()).booleanValue()) {
                this.originalPos = this.getOverlapPos();
            } else {
                this.originalPos.add(this.getPlayerPos());
            }

            Iterator iterator = this.originalPos.iterator();

            while (iterator.hasNext()) {
                BlockPos pos = (BlockPos) iterator.next();

                if (Burrow.mc.world.getBlockState(new BlockPos(Burrow.mc.player.posX, Burrow.mc.player.posY, Burrow.mc.player.posZ)).getBlock().equals(Blocks.OBSIDIAN) || this.intersectsWithEntity(pos)) {
                    this.setEnabled(false);
                }
            }

            this.tick = 0;
        }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock(this.getSelectedBlock())) == -1) {
                this.setEnabled(false);
                ChatUtils.sendMessage(new ChatMessage("Could not find block", false, 1));
            } else {
                int old;
                Iterator iterator;
                BlockPos pos;

                if (((String) this.mode.getValue()).equals("Spread")) {
                    switch (this.tick) {
                    case 0:
                        Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + this.heights[0], Burrow.mc.player.posZ, true));
                        break;

                    case 1:
                        Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + this.heights[1], Burrow.mc.player.posZ, true));
                        break;

                    case 2:
                        Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + this.heights[2], Burrow.mc.player.posZ, true));
                        break;

                    case 3:
                        Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + this.heights[3], Burrow.mc.player.posZ, true));
                        break;

                    case 4:
                        old = Burrow.mc.player.inventory.currentItem;
                        InventoryUtils.switchToSlotGhost(InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock(this.getSelectedBlock())));
                        iterator = this.originalPos.iterator();

                        while (iterator.hasNext()) {
                            pos = (BlockPos) iterator.next();
                            BlockUtils.placeBlock(pos, true);
                        }

                        Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + ((Number) this.offset.getValue()).doubleValue(), Burrow.mc.player.posZ, false));
                        InventoryUtils.switchToSlotGhost(old);
                        this.setEnabled(false);
                    }

                    ++this.tick;
                }

                if (((String) this.mode.getValue()).equals("Instant")) {
                    Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + this.heights[0], Burrow.mc.player.posZ, true));
                    Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + this.heights[1], Burrow.mc.player.posZ, true));
                    Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + this.heights[2], Burrow.mc.player.posZ, true));
                    Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + this.heights[3], Burrow.mc.player.posZ, true));
                    old = Burrow.mc.player.inventory.currentItem;
                    InventoryUtils.switchToSlotGhost(InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock(this.getSelectedBlock())));
                    ChatUtils.sendMessage(new ChatMessage("Placing burrow", true, 696969));
                    iterator = this.originalPos.iterator();

                    while (iterator.hasNext()) {
                        pos = (BlockPos) iterator.next();
                        BlockUtils.placeBlock(pos, true);
                    }

                    Burrow.mc.player.connection.sendPacket(new Position(Burrow.mc.player.posX, Burrow.mc.player.posY + ((Number) this.offset.getValue()).doubleValue(), Burrow.mc.player.posZ, false));
                    InventoryUtils.switchToSlotGhost(old);
                    this.setEnabled(false);
                }

            }
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!NullUtils.nullCheck()) {
            if (event.getPacket() instanceof CPacketPlayer) {
                CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                ICPacketPlayer inter = (ICPacketPlayer) packet;

                if (((String) this.mode.getValue()).equals("Intercept")) {
                    switch (this.tick) {
                    case 0:
                        inter.setY(Burrow.mc.player.posY + this.heights[0]);
                        break;

                    case 1:
                        inter.setY(Burrow.mc.player.posY + this.heights[1]);
                        break;

                    case 2:
                        inter.setY(Burrow.mc.player.posY + this.heights[2]);
                        break;

                    case 3:
                        inter.setY(Burrow.mc.player.posY + this.heights[3]);
                        break;

                    case 4:
                        int old = Burrow.mc.player.inventory.currentItem;

                        InventoryUtils.switchToSlotGhost(InventoryUtils.getHotbarItemSlot(Item.getItemFromBlock(this.getSelectedBlock())));
                        Iterator iterator = this.originalPos.iterator();

                        while (iterator.hasNext()) {
                            BlockPos pos = (BlockPos) iterator.next();

                            BlockUtils.placeBlock(pos, true);
                        }

                        inter.setY(Burrow.mc.player.posY + ((Number) this.offset.getValue()).doubleValue());
                        InventoryUtils.switchToSlotGhost(old);
                        this.setEnabled(false);
                    }

                    ++this.tick;
                }
            }

        }
    }

    Block getSelectedBlock() {
        String b = (String) this.block.getValue();

        return b.equals("EnderChest") ? Blocks.ENDER_CHEST : Blocks.OBSIDIAN;
    }

    boolean intersectsWithEntity(BlockPos pos) {
        Iterator iterator = Burrow.mc.world.loadedEntityList.iterator();

        Entity entity;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entity = (Entity) iterator.next();
        } while (entity.equals(Burrow.mc.player) || entity instanceof EntityItem || !(new AxisAlignedBB(pos)).intersects(entity.getEntityBoundingBox()));

        return true;
    }

    BlockPos getPlayerPos() {
        double decimalPoint = Burrow.mc.player.posY - Math.floor(Burrow.mc.player.posY);

        return new BlockPos(Burrow.mc.player.posX, decimalPoint > 0.8D ? Math.floor(Burrow.mc.player.posY) + 1.0D : Math.floor(Burrow.mc.player.posY), Burrow.mc.player.posZ);
    }

    List getOverlapPos() {
        ArrayList positions = new ArrayList();
        double decimalX = Burrow.mc.player.posX - Math.floor(Burrow.mc.player.posX);
        double decimalZ = Burrow.mc.player.posZ - Math.floor(Burrow.mc.player.posZ);
        int offX = this.calcOffset(decimalX);
        int offZ = this.calcOffset(decimalZ);

        positions.add(this.getPlayerPos());

        for (int x = 0; x <= Math.abs(offX); ++x) {
            for (int z = 0; z <= Math.abs(offZ); ++z) {
                int properX = x * offX;
                int properZ = z * offZ;

                positions.add(this.getPlayerPos().add(properX, 0, properZ));
            }
        }

        return positions;
    }

    int calcOffset(double dec) {
        return dec >= 0.7D ? 1 : (dec <= 0.3D ? -1 : 0);
    }
}
