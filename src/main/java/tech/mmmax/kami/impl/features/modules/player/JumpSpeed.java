package tech.mmmax.kami.impl.features.modules.player;

import java.util.function.Consumer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.MoveEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class JumpSpeed extends Module {

    Timer timer = new Timer();
    Value force = (new ValueBuilder()).withDescriptor("Force").withValue(Integer.valueOf(5)).withRange(Integer.valueOf(0), Integer.valueOf(20)).register(this);
    Value delay = (new ValueBuilder()).withDescriptor("Delay").withValue(Integer.valueOf(50)).withRange(Integer.valueOf(1), Integer.valueOf(1000)).withAction((val) -> {
        this.timer.setDelay(((Number) val.getValue()).longValue());
    }).register(this);
    Value mode = (new ValueBuilder()).withDescriptor("Mode").withValue("Ground").withModes(new String[] { "Ground", "OffGround", "Always"}).register(this);
    Value jumpMode = (new ValueBuilder()).withDescriptor("Jump Mode").withValue("Move").withModes(new String[] { "Move", "Tick"}).register(this);
    Value speed = (new ValueBuilder()).withDescriptor("Speed").withValue(Integer.valueOf(5)).withRange(Integer.valueOf(0), Integer.valueOf(100)).register(this);
    Value speedMode = (new ValueBuilder()).withDescriptor("Speed Mode").withValue("Move").withModes(new String[] { "Move", "Motion"}).register(this);

    public JumpSpeed() {
        super("JumpSpeed", Feature.Category.Player);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (JumpSpeed.mc.player.moveForward != 0.0F && JumpSpeed.mc.player.onGround && this.timer.isPassed() && ((String) this.jumpMode.getValue()).equals("Tick")) {
                JumpSpeed.mc.player.jump();
                this.timer.resetDelay();
            }

        }
    }

    @SubscribeEvent
    public void moveEvent(MoveEvent event) {
        if (!NullUtils.nullCheck()) {
            if (!JumpSpeed.mc.player.isInLava() && !JumpSpeed.mc.player.isInWater() && !JumpSpeed.mc.player.isOnLadder()) {
                if (JumpSpeed.mc.player.moveForward != 0.0F) {
                    if (JumpSpeed.mc.player.onGround) {
                        if (this.timer.isPassed() && ((String) this.jumpMode.getValue()).equals("Tick")) {
                            JumpSpeed.mc.player.jump();
                            this.timer.resetDelay();
                        }

                        if (((String) this.speedMode.getValue()).equals("Move")) {
                            event.x *= (double) (((Number) this.speed.getValue()).floatValue() / 50.0F);
                            event.z *= (double) (((Number) this.speed.getValue()).floatValue() / 50.0F);
                            event.moved = true;
                        }
                    }

                    if (JumpSpeed.mc.player.onGround && ((String) this.mode.getValue()).equals("Ground") || !JumpSpeed.mc.player.onGround && ((String) this.mode.getValue()).equals("OffGround") || ((String) this.mode.getValue()).equals("Always")) {
                        event.y = -((Number) this.force.getValue()).doubleValue();
                        event.moved = true;
                    }
                }

            }
        }
    }

    public String getHudInfo() {
        return (String) this.mode.getValue();
    }
}
