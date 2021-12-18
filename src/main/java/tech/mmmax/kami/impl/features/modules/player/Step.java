package tech.mmmax.kami.impl.features.modules.player;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class Step extends Module {

    Value stepHeight = (new ValueBuilder()).withDescriptor("Step Height").withValue(Double.valueOf(2.1D)).withRange(Double.valueOf(0.1D), Integer.valueOf(7)).register(this);
    Value mode = (new ValueBuilder()).withDescriptor("Mode").withValue("Vanilla").withModes(new String[] { "Vanilla", "NCP"}).register(this);
    float oldVal = 0.0F;

    public Step() {
        super("Step", Feature.Category.Player);
    }

    public void onEnable() {
        super.onEnable();
        this.oldVal = 0.5F;
    }

    public void onDisable() {
        super.onDisable();
        if (!NullUtils.nullCheck()) {
            Step.mc.player.stepHeight = this.oldVal;
        }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (((String) this.mode.getValue()).equals("Vanilla")) {
                Step.mc.player.stepHeight = ((Number) this.stepHeight.getValue()).floatValue();
            }

        }
    }

    public String getHudInfo() {
        return (String) this.mode.getValue();
    }
}
