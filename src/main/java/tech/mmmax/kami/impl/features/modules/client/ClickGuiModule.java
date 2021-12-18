package tech.mmmax.kami.impl.features.modules.client;

import java.awt.Color;
import java.util.function.Consumer;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.gui.theme.IColorScheme;
import tech.mmmax.kami.api.gui.theme.IMetrics;
import tech.mmmax.kami.api.utils.color.RainbowUtil;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.gui.ClickGui;
import tech.mmmax.kami.impl.gui.renderer.Renderer;

public class ClickGuiModule extends Module implements IColorScheme, IMetrics {

    public static ClickGuiModule INSTANCE;
    Value textShadow = (new ValueBuilder()).withDescriptor("Text Shadow").withValue(Boolean.valueOf(false)).register(this);
    public Value rainbowSat = (new ValueBuilder()).withDescriptor("Sat").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(0), Integer.valueOf(1)).register(this);
    public Value rainbowBri = (new ValueBuilder()).withDescriptor("Bri").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(0), Integer.valueOf(1)).register(this);
    Value rainbow = (new ValueBuilder()).withDescriptor("Rainbow").withValue(Boolean.valueOf(false)).withAction((val) -> {
        this.rainbowSat.setActive(((Boolean) val.getValue()).booleanValue());
        this.rainbowBri.setActive(((Boolean) val.getValue()).booleanValue());
    }).register(this);
    Value color = (new ValueBuilder()).withDescriptor("Color").withValue(new Color(32, 159, 220)).register(this);
    Value outlineColor = (new ValueBuilder()).withDescriptor("Outline Color").withValue(new Color(32, 159, 220)).register(this);
    Value frameText = (new ValueBuilder()).withDescriptor("Frame Text").withValue(Integer.valueOf(255)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value text = (new ValueBuilder()).withDescriptor("Text").withValue(Integer.valueOf(0)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value frameBackground = (new ValueBuilder()).withDescriptor("Background").withValue(Integer.valueOf(255)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value frameBackgroundAlpha = (new ValueBuilder()).withDescriptor("Background Alpha").withValue(Integer.valueOf(150)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value backgroundSecondary = (new ValueBuilder()).withDescriptor("Background S").withValue(Integer.valueOf(255)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value backgroundSecondaryAlpha = (new ValueBuilder()).withDescriptor("Background S Alpha").withValue(Integer.valueOf(150)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value backgroundTertiary = (new ValueBuilder()).withDescriptor("Background T").withValue(Integer.valueOf(255)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value backgroundTertiaryAlpha = (new ValueBuilder()).withDescriptor("Background T Alpha").withValue(Integer.valueOf(150)).withRange(Integer.valueOf(0), Integer.valueOf(255)).register(this);
    Value width = (new ValueBuilder()).withDescriptor("Width").withValue(Integer.valueOf(120)).withRange(Integer.valueOf(80), Integer.valueOf(200)).register(this);
    Value spacing = (new ValueBuilder()).withDescriptor("Spacing").withValue(Integer.valueOf(2)).withRange(Integer.valueOf(0), Integer.valueOf(4)).register(this);
    Value inBSpacing = (new ValueBuilder()).withDescriptor("In Spacing").withValue(Integer.valueOf(1)).withRange(Integer.valueOf(0), Integer.valueOf(4)).register(this);

    public ClickGuiModule() {
        super("ClickGUI", Feature.Category.Client);
        this.getBind().setKey(22);
        ClickGuiModule.INSTANCE = this;
    }

    public void onEnable() {
        super.onEnable();
        ClickGui.INSTANCE.enterGui(this, this, new Renderer());
        this.setEnabled(false);
    }

    public Color getMainColor(int pos) {
        return ((Boolean) this.rainbow.getValue()).booleanValue() ? RainbowUtil.INSTANCE.getColor(((Number) this.rainbowSat.getValue()).floatValue(), ((Number) this.rainbowBri.getValue()).floatValue()) : (Color) this.color.getValue();
    }

    public Color getOutlineColor() {
        return ((Boolean) this.rainbow.getValue()).booleanValue() ? RainbowUtil.INSTANCE.getColor(((Number) this.rainbowSat.getValue()).floatValue(), ((Number) this.rainbowBri.getValue()).floatValue()) : (Color) this.outlineColor.getValue();
    }

    public Color getButtonColor() {
        return this.getMainColor(0);
    }

    public Color getBackgroundColor() {
        return new Color(((Number) this.frameBackground.getValue()).intValue(), ((Number) this.frameBackground.getValue()).intValue(), ((Number) this.frameBackground.getValue()).intValue(), ((Number) this.frameBackgroundAlpha.getValue()).intValue());
    }

    public Color getSecondaryBackgroundColor() {
        return new Color(((Number) this.backgroundSecondary.getValue()).intValue(), ((Number) this.backgroundSecondary.getValue()).intValue(), ((Number) this.backgroundSecondary.getValue()).intValue(), ((Number) this.backgroundSecondaryAlpha.getValue()).intValue());
    }

    public Color getTertiaryBackgroundColor() {
        return new Color(((Number) this.backgroundTertiary.getValue()).intValue(), ((Number) this.backgroundTertiary.getValue()).intValue(), ((Number) this.backgroundTertiary.getValue()).intValue(), ((Number) this.backgroundTertiaryAlpha.getValue()).intValue());
    }

    public Color getTextColor() {
        return new Color(((Number) this.text.getValue()).intValue(), ((Number) this.text.getValue()).intValue(), ((Number) this.text.getValue()).intValue(), 255);
    }

    public Color getTextColorHighlight() {
        return new Color(((Number) this.frameText.getValue()).intValue(), ((Number) this.frameText.getValue()).intValue(), ((Number) this.frameText.getValue()).intValue());
    }

    public Color getTextColorActive() {
        return this.getMainColor(0);
    }

    public boolean doesTextShadow() {
        return ((Boolean) this.textShadow.getValue()).booleanValue();
    }

    public int getSpacing() {
        return ((Number) this.spacing.getValue()).intValue();
    }

    public int getBetweenSpacing() {
        return ((Number) this.inBSpacing.getValue()).intValue();
    }

    public int getSettingSpacing() {
        return 0;
    }

    public int getFrameWidth() {
        return ((Number) this.width.getValue()).intValue();
    }

    public int getButtonHeight() {
        return 12;
    }

    public int getFrameHeight() {
        return this.getButtonHeight() + 2;
    }

    public float getOutlineWidth() {
        return 1.0F;
    }
}
