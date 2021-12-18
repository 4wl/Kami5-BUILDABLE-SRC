package tech.mmmax.kami.impl.features.modules.player;

import java.util.function.Consumer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.MoveEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.utils.player.PlayerUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class YPort extends Module {

    Timer forceTimer = new Timer();
    Timer jumpTimer = new Timer();
    Value speed = (new ValueBuilder()).withDescriptor("Speed").withValue(Double.valueOf(0.07D)).withRange(Double.valueOf(0.01D), Double.valueOf(0.3D)).register(this);
    Value forceDelay = (new ValueBuilder()).withDescriptor("Force Delay").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(1), Integer.valueOf(1000)).withAction((s) -> {
        this.forceTimer.setDelay(((Number) s.getValue()).longValue());
    }).register(this);
    Value jumpDelay = (new ValueBuilder()).withDescriptor("Jump Delay").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(1), Integer.valueOf(1000)).withAction((s) -> {
        this.jumpTimer.setDelay(((Number) s.getValue()).longValue());
    }).register(this);
    Value forceMode = (new ValueBuilder()).withDescriptor("Force Mode").withValue("Tick").withModes(new String[] { "Tick", "Move", "MoveMotion"}).register(this);
    Value force = (new ValueBuilder()).withDescriptor("Force").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(0), Integer.valueOf(5)).register(this);
    Value height = (new ValueBuilder()).withDescriptor("Height").withValue(Integer.valueOf(3)).withRange(Integer.valueOf(1), Integer.valueOf(5)).register(this);

    public YPort() {
        super("YPort", Feature.Category.Player);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (PlayerUtils.isMoving(YPort.mc.player)) {
                if (YPort.mc.player.onGround) {
                    if (this.jumpTimer.isPassed()) {
                        YPort.mc.player.jump();
                        PlayerUtils.setSpeed(YPort.mc.player, PlayerUtils.getDefaultMoveSpeed() + ((Number) this.speed.getValue()).doubleValue());
                        this.jumpTimer.resetDelay();
                    }
                } else if (((String) this.forceMode.getValue()).equals("Tick") && (double) YPort.mc.player.fallDistance <= ((Number) this.height.getValue()).doubleValue() && this.forceTimer.isPassed()) {
                    YPort.mc.player.motionY -= ((Number) this.force.getValue()).doubleValue();
                    this.forceTimer.resetDelay();
                }
            }

        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (!NullUtils.nullCheck()) {
            if (PlayerUtils.isMoving(YPort.mc.player) && !YPort.mc.player.onGround && (double) YPort.mc.player.fallDistance <= ((Number) this.height.getValue()).doubleValue()) {
                if (((String) this.forceMode.getValue()).equals("Move") && this.forceTimer.isPassed()) {
                    event.y = -((Number) this.force.getValue()).doubleValue();
                    this.forceTimer.resetDelay();
                    event.moved = true;
                }

                if (((String) this.forceMode.getValue()).equals("MoveMotion") && this.forceTimer.isPassed()) {
                    YPort.mc.player.motionY -= ((Number) this.force.getValue()).doubleValue();
                    this.forceTimer.resetDelay();
                    event.moved = true;
                }
            }

        }
    }

    boolean isInHeight() {
        for (double y = 0.0D; y < ((Number) this.height.getValue()).doubleValue() + 0.5D; y += 0.01D) {
            if (!YPort.mc.world.getCollisionBoxes(YPort.mc.player, YPort.mc.player.getEntityBoundingBox().offset(0.0D, -y, 0.0D)).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public String getHudInfo() {
        return (String) this.forceMode.getValue();
    }
}
