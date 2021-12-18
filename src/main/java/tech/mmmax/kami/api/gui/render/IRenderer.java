package tech.mmmax.kami.api.gui.render;

import java.awt.Color;
import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.widget.impl.BindWidget;
import tech.mmmax.kami.api.gui.widget.impl.BooleanWidget;
import tech.mmmax.kami.api.gui.widget.impl.ColorWidget;
import tech.mmmax.kami.api.gui.widget.impl.ComboBoxWidget;
import tech.mmmax.kami.api.gui.widget.impl.SliderWidget;
import tech.mmmax.kami.api.gui.widget.impl.TextEntryWidget;
import tech.mmmax.kami.impl.gui.components.module.FeatureButton;

public interface IRenderer {

    void preRender(Context context);

    void renderFrameTitle(Context context, Rect rect, MouseHelper mousehelper, String s, boolean flag);

    void renderFrameOutline(Context context, Rect rect, MouseHelper mousehelper);

    void renderFrame(Context context, Rect rect, MouseHelper mousehelper);

    void renderBooleanWidget(BooleanWidget booleanwidget, Context context, Rect rect, MouseHelper mousehelper);

    void renderBindWidget(BindWidget bindwidget, Context context, Rect rect, MouseHelper mousehelper);

    void renderFeatureButton(FeatureButton featurebutton, Context context, Rect rect, MouseHelper mousehelper);

    void renderComboBox(ComboBoxWidget comboboxwidget, Context context, Rect rect, MouseHelper mousehelper);

    void renderSliderWidget(SliderWidget sliderwidget, Context context, Rect rect, Rect rect1, MouseHelper mousehelper);

    void renderColorWidget(ColorWidget colorwidget, Context context, boolean flag, Rect rect, Rect rect1, Rect rect2, Rect rect3, Rect rect4, Rect rect5);

    void renderStringWidget(TextEntryWidget textentrywidget, Context context, Rect rect, MouseHelper mousehelper);

    int getTextWidth(String s);

    int getTextHeight(String s);

    void renderText(String s, float f, float f1, Color color, boolean flag);

    void renderRect(Rect rect, Color color, Color color1, IRenderer.RectMode irenderer_rectmode, Context context);

    void scissorRect(Rect rect);

    public static enum RectMode {

        Fill, Outline;
    }
}
