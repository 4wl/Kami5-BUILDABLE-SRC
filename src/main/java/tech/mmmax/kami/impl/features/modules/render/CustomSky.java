package tech.mmmax.kami.impl.features.modules.render;

import java.awt.Color;
import java.util.function.Consumer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.RenderSkyEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.gui.ClickGui;

public class CustomSky extends Module {

    Value time = (new ValueBuilder()).withDescriptor("Time").withValue(Integer.valueOf(1000)).withRange(Integer.valueOf(0), Integer.valueOf(10000)).register(this);
    Value red = (new ValueBuilder()).withDescriptor("Red").withValue(Integer.valueOf(255)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value green = (new ValueBuilder()).withDescriptor("Green").withValue(Integer.valueOf(255)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value blue = (new ValueBuilder()).withDescriptor("Blue").withValue(Integer.valueOf(255)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value mode = (new ValueBuilder()).withDescriptor("Mode").withValue("Auto").withModes(new String[] { "Auto", "Custom", "Time"}).withAction((set) -> {
        this.time.setActive(((String) set.getValue()).equals("Time"));
        this.red.setActive(((String) set.getValue()).equals("Custom"));
        this.green.setActive(((String) set.getValue()).equals("Custom"));
        this.blue.setActive(((String) set.getValue()).equals("Custom"));
    }).register(this);

    public CustomSky() {
        super("Custom Sky", Feature.Category.Render);
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (((String) this.mode.getValue()).equals("Time")) {
                CustomSky.mc.world.setWorldTime(((Number) this.time.getValue()).longValue());
            }

        }
    }

    @SubscribeEvent
    public void onRenderSky(RenderSkyEvent event) {
        if (!((String) this.mode.getValue()).equals(this.time)) {
            Color color = ((String) this.mode.getValue()).equals("Custom") ? new Color(((Number) this.red.getValue()).intValue(), ((Number) this.green.getValue()).intValue(), ((Number) this.blue.getValue()).intValue()) : ClickGui.CONTEXT.getColorScheme().getMainColor(0);

            event.setRed((double) color.getRed());
            event.setGreen((double) color.getGreen());
            event.setBlue((double) color.getBlue());
            event.setCanceled(true);
        }

    }
}
