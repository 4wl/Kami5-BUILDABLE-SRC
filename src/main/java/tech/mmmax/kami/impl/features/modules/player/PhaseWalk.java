package tech.mmmax.kami.impl.features.modules.player;

import java.util.function.Consumer;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.MoveEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.utils.player.PlayerUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class PhaseWalk extends Module {

    Timer timer = new Timer();
    Value edgeEnable = (new ValueBuilder()).withDescriptor("Edge Enable").withValue(Boolean.valueOf(false)).register(this);
    Value mode = (new ValueBuilder()).withDescriptor("Mode").withValue("Clip").withModes(new String[] { "Clip", "Smooth"}).register(this);
    Value delay = (new ValueBuilder()).withDescriptor("Delay").withValue(Integer.valueOf(200)).withRange(Integer.valueOf(0), Integer.valueOf(1000)).withAction((s) -> {
        this.timer.setDelay(((Number) s.getValue()).longValue());
    }).register(this);
    Value attempts = (new ValueBuilder()).withDescriptor("Attempts").withValue(Integer.valueOf(5)).withRange(Integer.valueOf(0), Integer.valueOf(10)).register(this);
    Value cancelPlayer = (new ValueBuilder()).withDescriptor("Cancel").withValue(Boolean.valueOf(true)).register(this);
    Value handleTeleport = (new ValueBuilder()).withDescriptor("Handle Teleport").withValue("All").withModes(new String[] { "All", "Below", "Above", "NoBand", "Last", "Cancel", "None"}).register(this);
    Value limitAmount = (new ValueBuilder()).withDescriptor("Limit Amount").withValue(Double.valueOf(0.3D)).withRange(Integer.valueOf(0), Integer.valueOf(1)).register(this);
    Value speed = (new ValueBuilder()).withDescriptor("Speed").withValue(Integer.valueOf(3)).withRange(Integer.valueOf(1), Integer.valueOf(10)).register(this);
    Value autoSpeed = (new ValueBuilder()).withDescriptor("Auto Speed").withValue(Boolean.valueOf(true)).register(this);
    boolean cancel = false;
    int teleportID = 0;

    public PhaseWalk() {
        super("PhaseWalk", Feature.Category.Player);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!NullUtils.nullCheck()) {
            if (event.getPacket() instanceof CPacketPlayer && this.cancel && ((Boolean) this.cancelPlayer.getValue()).booleanValue()) {
                event.setCanceled(true);
            }

            if (event.getPacket() instanceof CPacketConfirmTeleport && ((String) this.handleTeleport.getValue()).equals("Cancel")) {
                event.setCanceled(true);
            }

            if (event.getPacket() instanceof SPacketPlayerPosLook) {
                this.teleportID = ((SPacketPlayerPosLook) event.getPacket()).getTeleportId();
                if (((String) this.handleTeleport.getValue()).equals("All")) {
                    PhaseWalk.mc.getConnection().sendPacket(new CPacketConfirmTeleport(this.teleportID - 1));
                    PhaseWalk.mc.getConnection().sendPacket(new CPacketConfirmTeleport(this.teleportID));
                    PhaseWalk.mc.getConnection().sendPacket(new CPacketConfirmTeleport(this.teleportID + 1));
                }

                if (((String) this.handleTeleport.getValue()).equals("Below")) {
                    PhaseWalk.mc.getConnection().sendPacket(new CPacketConfirmTeleport(this.teleportID + -1));
                }

                if (((String) this.handleTeleport.getValue()).equals("Above")) {
                    PhaseWalk.mc.getConnection().sendPacket(new CPacketConfirmTeleport(this.teleportID + 1));
                }

                if (((String) this.handleTeleport.getValue()).equals("NoBand")) {
                    PhaseWalk.mc.getConnection().sendPacket(new Position(0.0D, 1337.0D, 0.0D, PhaseWalk.mc.player.onGround));
                    PhaseWalk.mc.getConnection().sendPacket(new CPacketConfirmTeleport(this.teleportID + 1));
                }
            }

        }
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            PhaseWalk.mc.player.motionX = 0.0D;
            PhaseWalk.mc.player.motionY = 0.0D;
            PhaseWalk.mc.player.motionZ = 0.0D;
            if (((String) this.mode.getValue()).equals("Clip")) {
                if (this.shouldPacket()) {
                    if (this.timer.isPassed()) {
                        double[] forward = PlayerUtils.forward(this.getSpeed());

                        for (int i = 0; i < ((Number) this.attempts.getValue()).intValue(); ++i) {
                            this.sendPackets(PhaseWalk.mc.player.posX + forward[0], PhaseWalk.mc.player.posY + this.getUpMovement(), PhaseWalk.mc.player.posZ + forward[1]);
                        }

                        this.timer.resetDelay();
                    }
                } else {
                    this.cancel = false;
                }
            }

        }
    }

    double getUpMovement() {
        return (double) (PhaseWalk.mc.gameSettings.keyBindJump.isKeyDown() ? 1 : (PhaseWalk.mc.gameSettings.keyBindSneak.isKeyDown() ? -1 : 0)) * this.getSpeed();
    }

    public void sendPackets(double x, double y, double z) {
        this.cancel = false;
        PhaseWalk.mc.getConnection().sendPacket(new Position(x, y, z, PhaseWalk.mc.player.onGround));
        PhaseWalk.mc.getConnection().sendPacket(new Position(0.0D, 1337.0D, 0.0D, PhaseWalk.mc.player.onGround));
        this.cancel = true;
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (!NullUtils.nullCheck()) {
            if (this.shouldPacket()) {
                if (((String) this.mode.getValue()).equals("Smooth")) {
                    double[] forward = PlayerUtils.forward(this.getSpeed());

                    for (int i = 0; i < ((Number) this.attempts.getValue()).intValue(); ++i) {
                        this.sendPackets(PhaseWalk.mc.player.posX + forward[0], PhaseWalk.mc.player.posY + this.getUpMovement(), PhaseWalk.mc.player.posZ + forward[1]);
                    }
                }

                event.x = 0.0D;
                event.y = 0.0D;
                event.z = 0.0D;
            }

        }
    }

    double getSpeed() {
        return ((Boolean) this.autoSpeed.getValue()).booleanValue() ? PlayerUtils.getDefaultMoveSpeed() / 10.0D : ((Number) this.speed.getValue()).doubleValue() / 100.0D;
    }

    boolean shouldPacket() {
        return !((Boolean) this.edgeEnable.getValue()).booleanValue() || PhaseWalk.mc.player.collidedHorizontally;
    }

    public String getHudInfo() {
        return (String) this.mode.getValue();
    }
}
