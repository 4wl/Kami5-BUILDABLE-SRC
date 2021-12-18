package tech.mmmax.kami.impl.features.modules.client;

import java.awt.Color;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.render.IRenderer;
import tech.mmmax.kami.api.gui.theme.IColorScheme;
import tech.mmmax.kami.api.gui.theme.IMetrics;
import tech.mmmax.kami.api.gui.widget.impl.BindWidget;
import tech.mmmax.kami.api.gui.widget.impl.BooleanWidget;
import tech.mmmax.kami.api.gui.widget.impl.ColorWidget;
import tech.mmmax.kami.api.gui.widget.impl.ComboBoxWidget;
import tech.mmmax.kami.api.gui.widget.impl.SliderWidget;
import tech.mmmax.kami.api.gui.widget.impl.TextEntryWidget;
import tech.mmmax.kami.impl.gui.components.module.FeatureButton;

public class SixGuiModule extends Module implements IColorScheme, IMetrics, IRenderer {

    public SixGuiModule() {
        super("Six Gui", Feature.Category.Client);
    }

    public void preRender(Context context) {}

    public void renderFrameTitle(Context context, Rect rect, MouseHelper mouse, String title, boolean open) {}

    public void renderFrameOutline(Context context, Rect rect, MouseHelper mouse) {}

    public void renderFrame(Context context, Rect rect, MouseHelper mouse) {}

    public void renderBooleanWidget(BooleanWidget widget, Context context, Rect rect, MouseHelper mouse) {}

    public void renderBindWidget(BindWidget widget, Context context, Rect rect, MouseHelper mouse) {}

    public void renderFeatureButton(FeatureButton button, Context context, Rect rect, MouseHelper mouse) {}

    public void renderComboBox(ComboBoxWidget widget, Context context, Rect rect, MouseHelper mouseHelper) {}

    public void renderSliderWidget(SliderWidget widget, Context context, Rect rect, Rect sliderRect, MouseHelper mouse) {}

    public void renderColorWidget(ColorWidget widget, Context context, boolean open, Rect headerRect, Rect dims, Rect container, Rect alphaSlider, Rect hueSlider, Rect colorSquare) {}

    public void renderStringWidget(TextEntryWidget widget, Context context, Rect rect, MouseHelper mouse) {}

    public int getTextWidth(String text) {
        return 0;
    }

    public int getTextHeight(String text) {
        return 0;
    }

    public void renderText(String text, float x, float y, Color color, boolean shadow) {}

    public void renderRect(Rect rect, Color top, Color bottom, IRenderer.RectMode mode, Context context) {}

    public void scissorRect(Rect dims) {}

    public Color getMainColor(int pos) {
        return null;
    }

    public Color getOutlineColor() {
        return null;
    }

    public Color getButtonColor() {
        return null;
    }

    public Color getBackgroundColor() {
        return null;
    }

    public Color getSecondaryBackgroundColor() {
        return null;
    }

    public Color getTertiaryBackgroundColor() {
        return null;
    }

    public Color getTextColor() {
        return null;
    }

    public Color getTextColorHighlight() {
        return null;
    }

    public Color getTextColorActive() {
        return null;
    }

    public boolean doesTextShadow() {
        return false;
    }

    public int getSpacing() {
        return 0;
    }

    public int getBetweenSpacing() {
        return 0;
    }

    public int getSettingSpacing() {
        return 0;
    }

    public int getFrameWidth() {
        return 0;
    }

    public int getButtonHeight() {
        return 0;
    }

    public int getFrameHeight() {
        return 0;
    }

    public float getOutlineWidth() {
        return 0.0F;
    }
}
