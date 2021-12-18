package tech.mmmax.kami.impl.features.modules.misc;

import java.awt.Color;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.utils.chat.ChatMessage;
import tech.mmmax.kami.api.utils.chat.ChatUtils;
import tech.mmmax.kami.api.utils.player.InventoryUtils;
import tech.mmmax.kami.api.utils.render.RenderUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class SpeedMine extends Module {

    Timer breakTimer = new Timer();
    Timer switchTimer = new Timer();
    Value render = (new ValueBuilder()).withDescriptor("Render").withValue(Boolean.valueOf(true)).register(this);
    Value animateFill = (new ValueBuilder()).withDescriptor("Animate Fill").withValue(Boolean.valueOf(true)).register(this);
    Value animateOutline = (new ValueBuilder()).withDescriptor("Animate Line").withValue(Boolean.valueOf(false)).register(this);
    Value delay = (new ValueBuilder()).withDescriptor("Delay").withValue(Integer.valueOf(200)).withRange(Integer.valueOf(20), Integer.valueOf(2000)).withAction((set) -> {
        this.breakTimer.setDelay(((Number) set.getValue()).longValue());
    }).register(this);
    Value breakAttempts = (new ValueBuilder()).withDescriptor("Break Attempts").withValue(Integer.valueOf(2)).withRange(Integer.valueOf(1), Integer.valueOf(5)).register(this);
    Value autoBreakGhostSwitch = (new ValueBuilder()).withDescriptor("Ghost Switch AB").withValue(Boolean.valueOf(false)).register(this);
    Value autoBreak = (new ValueBuilder()).withDescriptor("AutoBreak").withValue(Boolean.valueOf(false)).withAction((set) -> {
        this.delay.setActive(((Boolean) set.getValue()).booleanValue());
        this.autoBreakGhostSwitch.setActive(((Boolean) set.getValue()).booleanValue());
    }).register(this);
    Value stopCount = (new ValueBuilder()).withDescriptor("Stop count").withValue(Integer.valueOf(2)).withRange(Integer.valueOf(1), Integer.valueOf(4)).register(this);
    Value cancel = (new ValueBuilder()).withDescriptor("Cancel").withValue(Boolean.valueOf(false)).register(this);
    Value mode = (new ValueBuilder()).withDescriptor("Mode").withValue("Packet").withModes(new String[] { "Packet", "Instant", "InstantBypass"}).withAction((set) -> {
        this.autoBreak.setActive(set.getValue().contains("Instant"));
        this.delay.setActive(set.getValue().contains("Instant"));
        this.autoBreakGhostSwitch.setActive(set.getValue().contains("Instant"));
        this.stopCount.setActive(set.getValue().contains("Instant"));
        this.animateFill.setActive(set.getValue().equals("Packet"));
        this.animateOutline.setActive(set.getValue().equals("Packet"));
    }).register(this);
    Value unbreak = (new ValueBuilder()).withDescriptor("Unbreak").withValue(Boolean.valueOf(false)).register(this);
    Value ghostSwitch = (new ValueBuilder()).withDescriptor("Ghost Switch").withValue(Boolean.valueOf(true)).register(this);
    Value ghostSwitchPacket = (new ValueBuilder()).withDescriptor("Ghost Switch Packet").withValue(Boolean.valueOf(true)).register(this);
    Value debug = (new ValueBuilder()).withDescriptor("Debug").withValue(Boolean.valueOf(false)).register(this);
    Value outline = (new ValueBuilder()).withDescriptor("Outline").withValue(new Color(255, 255, 255, 255)).register(this);
    Value fill = (new ValueBuilder()).withDescriptor("Fill").withValue(new Color(158, 50, 255, 81)).register(this);
    BlockPos breaking;
    boolean packetCancel = false;
    EnumFacing face;
    int currentBreakAttempts = 0;
    boolean firstBreak = false;
    long startTime = 0L;

    public SpeedMine() {
        super("SpeedMine", Feature.Category.Misc);
    }

    public void onEnable() {
        super.onEnable();
        this.currentBreakAttempts = 0;
    }

    @SubscribeEvent
    public void onBlockClick(LeftClickBlock event) {
        if (!NullUtils.nullCheck()) {
            if (this.canBreak(event.getPos())) {
                if (this.breaking == null || !this.breaking.equals(event.getPos())) {
                    this.startTime = System.currentTimeMillis();
                }

                if (((String) this.mode.getValue()).equals("Packet")) {
                    if (this.breaking != null && ((Boolean) this.unbreak.getValue()).booleanValue()) {
                        SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.breaking, this.face));
                        SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, this.breaking, this.face));
                    }

                    SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, event.getPos(), event.getFace()));
                    SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFace()));
                    float i = SpeedMine.mc.world.getBlockState(event.getPos()).getBlockHardness(SpeedMine.mc.world, event.getPos()) * 20.0F * 2.0F;

                    this.switchTimer.setDelay((long) i);
                    this.switchTimer.resetDelay();
                }

                int i;

                if (((String) this.mode.getValue()).equals("Instant")) {
                    if (this.breaking == null || !this.breaking.equals(event.getPos())) {
                        this.packetCancel = false;
                        SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, event.getPos(), event.getFace()));
                        this.currentBreakAttempts = 0;
                        this.packetCancel = true;
                        this.firstBreak = true;
                    }

                    for (i = 0; i < ((Number) this.stopCount.getValue()).intValue(); ++i) {
                        SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFace()));
                    }

                    if (!this.firstBreak) {
                        ++this.currentBreakAttempts;
                    }
                }

                if (((String) this.mode.getValue()).equals("InstantBypass")) {
                    if (this.breaking == null || !this.breaking.equals(event.getPos())) {
                        this.packetCancel = false;
                        SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, event.getPos(), event.getFace()));
                        this.currentBreakAttempts = 0;
                        this.packetCancel = true;
                        this.firstBreak = true;
                    }

                    for (i = 0; i < ((Number) this.stopCount.getValue()).intValue(); ++i) {
                        SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFace()));
                    }

                    SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, event.getPos(), event.getFace()));
                    if (!this.firstBreak) {
                        ++this.currentBreakAttempts;
                    }
                }

                this.breaking = event.getPos();
                this.face = event.getFace();
                if (((Boolean) this.cancel.getValue()).booleanValue()) {
                    event.setCanceled(true);
                }
            }

        }
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (this.breaking != null) {
                if (SpeedMine.mc.world.getBlockState(this.breaking).getBlock() == Blocks.AIR) {
                    if (((String) this.mode.getValue()).equals("Packet")) {
                        this.breaking = null;
                    }

                    this.currentBreakAttempts = 0;
                    this.firstBreak = false;
                    return;
                }

                int oldSlot;
                int pickSlot;
                int i;

                if (this.switchTimer.isPassed()) {
                    if (((Boolean) this.ghostSwitch.getValue()).booleanValue()) {
                        oldSlot = -1;
                        pickSlot = SpeedMine.mc.player.inventory.currentItem;

                        for (i = 0; i < 9; ++i) {
                            if (SpeedMine.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemPickaxe) {
                                oldSlot = i;
                            }
                        }

                        if (oldSlot != -1) {
                            InventoryUtils.switchToSlotGhost(oldSlot);
                            SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.breaking, this.face));
                            InventoryUtils.switchToSlotGhost(pickSlot);
                            if (((Boolean) this.debug.getValue()).booleanValue()) {
                                ChatUtils.sendMessage(new ChatMessage("Switching to break block", false, 0));
                            }
                        }
                    }

                    this.switchTimer.resetDelay();
                    return;
                }

                if (((String) this.mode.getValue()).contains("Instant") && this.face != null && this.breakTimer.isPassed()) {
                    if (((Boolean) this.autoBreak.getValue()).booleanValue()) {
                        oldSlot = SpeedMine.mc.player.inventory.currentItem;
                        pickSlot = this.getPickSlot();
                        if (pickSlot == -1) {
                            return;
                        }

                        if (((Boolean) this.autoBreakGhostSwitch.getValue()).booleanValue()) {
                            InventoryUtils.switchToSlotGhost(pickSlot);
                        }

                        if (((Boolean) this.autoBreakGhostSwitch.getValue()).booleanValue() || SpeedMine.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemPickaxe) {
                            if (this.currentBreakAttempts <= ((Number) this.breakAttempts.getValue()).intValue()) {
                                if (((String) this.mode.getValue()).equals("Instant")) {
                                    for (i = 0; i < ((Number) this.stopCount.getValue()).intValue(); ++i) {
                                        SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.breaking, this.face));
                                    }

                                    ++this.currentBreakAttempts;
                                }

                                if (((String) this.mode.getValue()).equals("InstantBypass")) {
                                    for (i = 0; i < ((Number) this.stopCount.getValue()).intValue(); ++i) {
                                        SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.breaking, this.face));
                                    }

                                    SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, this.breaking, this.face));
                                    ++this.currentBreakAttempts;
                                }
                            } else if ((((String) this.mode.getValue()).equals("Instant") || ((String) this.mode.getValue()).equals("InstantBypass")) && !this.firstBreak) {
                                ChatUtils.sendMessage(new ChatMessage("Restarting block break", false, 0));
                                SpeedMine.mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, this.breaking, this.face));
                                this.breaking = null;
                                this.currentBreakAttempts = 0;
                            }
                        }

                        if (((Boolean) this.autoBreakGhostSwitch.getValue()).booleanValue()) {
                            InventoryUtils.switchToSlotGhost(oldSlot);
                        }
                    }

                    this.breakTimer.resetDelay();
                }
            }

        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!NullUtils.nullCheck()) {
            if ((((String) this.mode.getValue()).equals("Instant") || ((String) this.mode.getValue()).equals("InstantBypass")) && event.getPacket() instanceof CPacketPlayerDigging && ((CPacketPlayerDigging) event.getPacket()).getAction() == Action.START_DESTROY_BLOCK && this.packetCancel) {
                event.setCanceled(true);
            }

        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (this.breaking != null && ((Boolean) this.render.getValue()).booleanValue()) {
            if (SpeedMine.mc.world.getBlockState(this.breaking).getBlock() == Blocks.AIR && ((String) this.mode.getValue()).equals("Packet")) {
                this.breaking = null;
                return;
            }

            AxisAlignedBB fillBB = new AxisAlignedBB(this.breaking);
            AxisAlignedBB lineBB = new AxisAlignedBB(this.breaking);

            if (((String) this.mode.getValue()).equals("Packet")) {
                float breakTime = SpeedMine.mc.world.getBlockState(this.breaking).getBlockHardness(SpeedMine.mc.world, this.breaking) * 20.0F * 2.0F;
                double shrinkFactor = this.normalize((double) (System.currentTimeMillis() - this.startTime), 0.0D, (double) breakTime);

                shrinkFactor = MathHelper.clamp(shrinkFactor, 0.0D, 1.0D);
                if (((Boolean) this.animateFill.getValue()).booleanValue()) {
                    fillBB = fillBB.shrink(shrinkFactor);
                }

                if (((Boolean) this.animateOutline.getValue()).booleanValue()) {
                    lineBB = lineBB.shrink(shrinkFactor);
                }
            }

            RenderUtil.renderBB(7, fillBB, (Color) this.fill.getValue(), (Color) this.fill.getValue());
            RenderUtil.renderBB(3, lineBB, (Color) this.outline.getValue(), (Color) this.outline.getValue());
        }

    }

    public String getHudInfo() {
        return (String) this.mode.getValue();
    }

    boolean canBreak(BlockPos pos) {
        IBlockState blockState = SpeedMine.mc.world.getBlockState(pos);
        Block block = blockState.getBlock();

        return block.getBlockHardness(blockState, SpeedMine.mc.world, pos) != -1.0F;
    }

    int getPickSlot() {
        int pickSlot = -1;

        for (int i = 0; i < 9; ++i) {
            if (SpeedMine.mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemPickaxe) {
                pickSlot = i;
                break;
            }
        }

        return pickSlot;
    }

    double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}
