package tech.mmmax.kami.impl.features.modules.client;

import java.awt.Color;
import net.minecraft.util.math.MathHelper;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.color.ColorUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class HudColors extends Module {

    Value step = (new ValueBuilder()).withDescriptor("Step").withValue(Boolean.valueOf(false)).register(this);
    Value stepLength = (new ValueBuilder()).withDescriptor("Step Length").withValue(Integer.valueOf(30)).withRange(Integer.valueOf(10), Integer.valueOf(130)).register(this);
    Value stepSpeed = (new ValueBuilder()).withDescriptor("Step Speed").withValue(Integer.valueOf(30)).withRange(Integer.valueOf(1), Integer.valueOf(130)).register(this);
    Value mainColor = (new ValueBuilder()).withDescriptor("Main Color").withValue(new Color(0, 150, 255)).register(this);
    Value stepColor = (new ValueBuilder()).withDescriptor("Step Color").withValue(new Color(0, 150, 255)).register(this);
    static HudColors INSTANCE;

    public HudColors() {
        super("Hud Colors", Feature.Category.Client);
        HudColors.INSTANCE = this;
    }

    public static Color getTextColor(int y) {
        if (((Boolean) HudColors.INSTANCE.step.getValue()).booleanValue()) {
            double roundY = Math.sin(Math.toRadians((double) ((long) (y * ((Number) HudColors.INSTANCE.stepLength.getValue()).intValue()) + System.currentTimeMillis() / (long) ((Number) HudColors.INSTANCE.stepSpeed.getValue()).intValue())));

            roundY = Math.abs(roundY);
            return ColorUtil.interpolate((float) MathHelper.clamp(roundY, 0.0D, 1.0D), (Color) HudColors.INSTANCE.mainColor.getValue(), (Color) HudColors.INSTANCE.stepColor.getValue());
        } else {
            return (Color) HudColors.INSTANCE.mainColor.getValue();
        }
    }
}
