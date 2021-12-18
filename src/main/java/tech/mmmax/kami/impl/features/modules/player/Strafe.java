package tech.mmmax.kami.impl.features.modules.player;

import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.MoveEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.player.PlayerUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.mixin.mixins.access.IMinecraft;
import tech.mmmax.kami.mixin.mixins.access.ITimer;

public class Strafe extends Module {

    Value airSpeed = (new ValueBuilder()).withDescriptor("Air Speed").withValue(Double.valueOf(1.2D)).withRange(Integer.valueOf(1), Double.valueOf(1.3D)).register(this);
    Value downSpeed = (new ValueBuilder()).withDescriptor("Down Speed").withValue(Double.valueOf(1.2D)).withRange(Integer.valueOf(1), Double.valueOf(1.3D)).register(this);
    Value groundSpeed = (new ValueBuilder()).withDescriptor("Ground Speed").withValue(Double.valueOf(1.2D)).withRange(Integer.valueOf(1), Double.valueOf(1.3D)).register(this);
    Value jumpSpeed = (new ValueBuilder()).withDescriptor("Jump Speed").withValue(Double.valueOf(1.2D)).withRange(Integer.valueOf(1), Double.valueOf(1.3D)).register(this);
    Value useTimer = (new ValueBuilder()).withDescriptor("Use Timer").withValue(Boolean.valueOf(false)).register(this);
    Value timerAmount = (new ValueBuilder()).withDescriptor("Timer Amount").withValue(Double.valueOf(1.2D)).withRange(Integer.valueOf(1), Double.valueOf(1.3D)).register(this);
    float oldTickLength;
    ITimer timer;
    boolean jumpBoosting = false;

    public Strafe() {
        super("Strafe", Feature.Category.Player);
    }

    public void onEnable() {
        super.onEnable();
        this.timer = (ITimer) ((IMinecraft) Strafe.mc).getTimer();
        this.oldTickLength = this.timer.getTickLength();
    }

    public void onDisable() {
        super.onDisable();
        if (((Boolean) this.useTimer.getValue()).booleanValue()) {
            this.timer.setTickLength(this.oldTickLength);
        }

    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (((Boolean) this.useTimer.getValue()).booleanValue()) {
                this.timer.setTickLength(this.oldTickLength / ((Number) this.timerAmount.getValue()).floatValue());
            } else {
                this.timer.setTickLength(this.oldTickLength);
            }

            if (PlayerUtils.isMoving(Strafe.mc.player) && Strafe.mc.player.onGround) {
                Strafe.mc.player.jump();
            }

        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (!NullUtils.nullCheck()) {
            if (PlayerUtils.isMoving(Strafe.mc.player)) {
                float speed = Strafe.mc.player.onGround ? ((Number) this.groundSpeed.getValue()).floatValue() : (this.jumpBoosting ? ((Number) this.jumpSpeed.getValue()).floatValue() : (event.y > 0.0D ? ((Number) this.airSpeed.getValue()).floatValue() : ((Number) this.downSpeed.getValue()).floatValue()));
                double[] motion = PlayerUtils.forward(PlayerUtils.getDefaultMoveSpeed() * (double) speed);

                event.x = motion[0];
                event.z = motion[1];
            } else {
                event.x = 0.0D;
                event.z = 0.0D;
            }

            if (this.jumpBoosting) {
                this.jumpBoosting = false;
            }

        }
    }

    @SubscribeEvent
    public void onJump(LivingJumpEvent event) {
        this.jumpBoosting = true;
    }
}
