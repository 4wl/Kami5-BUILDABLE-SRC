package tech.mmmax.kami.impl.features.modules.player;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.MoveEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class ReverseStep extends Module {

    Value force = (new ValueBuilder()).withDescriptor("Force").withValue(Integer.valueOf(5)).withRange(Integer.valueOf(3), Integer.valueOf(20)).register(this);
    Value mode = (new ValueBuilder()).withDescriptor("Mode").withValue("Normal").withModes(new String[] { "Normal", "Bypass", "MoveEventCancel"}).register(this);

    public ReverseStep() {
        super("Reverse Step", Feature.Category.Player);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (!ReverseStep.mc.player.isInLava() && !ReverseStep.mc.player.isInWater() && !ReverseStep.mc.player.isOnLadder()) {
                if ((((String) this.mode.getValue()).equals("Normal") || ((String) this.mode.getValue()).equals("MoveEventCancel")) && ReverseStep.mc.player.onGround) {
                    ReverseStep.mc.player.motionY -= ((Number) this.force.getValue()).doubleValue();
                }

            }
        }
    }

    @SubscribeEvent
    public void moveEvent(MoveEvent event) {
        if (!NullUtils.nullCheck()) {
            if (!ReverseStep.mc.player.isInLava() && !ReverseStep.mc.player.isInWater() && !ReverseStep.mc.player.isOnLadder()) {
                if (((String) this.mode.getValue()).equals("MoveEventCancel") && ReverseStep.mc.player.onGround) {
                    event.y = 0.0D;
                }

                if (((String) this.mode.getValue()).equals("Bypass") && ReverseStep.mc.player.onGround && event.y < 0.1D) {
                    event.y = -((Number) this.force.getValue()).doubleValue();
                    event.moved = true;
                }

            }
        }
    }

    public String getHudInfo() {
        return (String) this.mode.getValue();
    }
}
