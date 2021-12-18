package tech.mmmax.kami.impl.features.modules.client;

import java.awt.Color;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
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
import tech.mmmax.kami.api.utils.MathUtil;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.color.ColorUtil;
import tech.mmmax.kami.api.utils.render.font.CFontRenderer;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.gui.ClickGui;
import tech.mmmax.kami.impl.gui.components.module.FeatureButton;

public class SecondGuiModule extends Module implements IColorScheme, IMetrics, IRenderer {

    static CFontRenderer fontRenderer = new CFontRenderer(FontModule.INSTANCE.font);
    Value textShadow = (new ValueBuilder()).withDescriptor("Text Shadow").withValue(Boolean.valueOf(true)).register(this);
    Value textColor = (new ValueBuilder()).withDescriptor("Text Color").withValue(new Color(255, 255, 255, 255)).register(this);
    Value color = (new ValueBuilder()).withDescriptor("Color").withValue(new Color(0, 179, 42, 255)).register(this);
    Value backgroundColor = (new ValueBuilder()).withDescriptor("Background Color").withValue(new Color(76, 76, 76, 150)).register(this);
    Value backgroundSecondary = (new ValueBuilder()).withDescriptor("Background Second").withValue(new Color(33, 33, 33, 150)).register(this);
    Value backgroundTertiary = (new ValueBuilder()).withDescriptor("Background Tert").withValue(new Color(3, 3, 3, 174)).register(this);
    Value outlineColor = (new ValueBuilder()).withDescriptor("Outline Color").withValue(new Color(0, 0, 0, 255)).register(this);
    Value width = (new ValueBuilder()).withDescriptor("Width").withValue(Integer.valueOf(100)).withRange(Integer.valueOf(60), Integer.valueOf(200)).register(this);
    Value gradientSize = (new ValueBuilder()).withDescriptor("Gradient Size").withValue(Integer.valueOf(100)).withRange(Integer.valueOf(0), Integer.valueOf(600)).register(this);
    Value bgColor = (new ValueBuilder()).withDescriptor("Background").withValue(new Color(0, 0, 0, 25)).register(this);

    public SecondGuiModule() {
        super("Second Gui", Feature.Category.Client);
    }

    public void onEnable() {
        if (!NullUtils.nullCheck()) {
            super.onEnable();
            ClickGui.INSTANCE.enterGui(this, this, this);
            this.setEnabled(false);
        }
    }

    public void preRender(Context context) {
        GlStateManager.disableAlpha();
        this.renderRect(new Rect(0, context.getScaledResolution().getScaledHeight() - ((Number) this.gradientSize.getValue()).intValue(), context.getScaledResolution().getScaledWidth(), ((Number) this.gradientSize.getValue()).intValue()), (Color) this.bgColor.getValue(), context.getColorScheme().getMainColor(0), IRenderer.RectMode.Fill, context);
        this.renderRect(new Rect(0, 0, context.getScaledResolution().getScaledWidth(), context.getScaledResolution().getScaledHeight() - ((Number) this.gradientSize.getValue()).intValue()), (Color) this.bgColor.getValue(), (Color) this.bgColor.getValue(), IRenderer.RectMode.Fill, context);
        GlStateManager.enableAlpha();
    }

    public void renderFrameTitle(Context context, Rect rect, MouseHelper mouse, String title, boolean open) {
        GL11.glLineWidth(1.0F);
        rect.setX(rect.getX() - 1);
        rect.setWidth(rect.getWidth() + 2);
        context.getRenderer().renderRect(rect, context.getColorScheme().getMainColor(rect.getY()), context.getColorScheme().getMainColor(rect.getY()), IRenderer.RectMode.Fill, context);
        context.getRenderer().renderRect(rect, context.getColorScheme().getOutlineColor(), context.getColorScheme().getOutlineColor(), IRenderer.RectMode.Outline, context);
        int centerY = (rect.getHeight() - context.getRenderer().getTextHeight(title)) / 2;

        context.getRenderer().renderText(title, (float) (rect.getX() + 2), (float) (rect.getY() + centerY), context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
    }

    public void renderFrameOutline(Context context, Rect rect, MouseHelper mouse) {}

    public void renderFrame(Context context, Rect rect, MouseHelper mouse) {
        context.getRenderer().renderRect(rect, context.getColorScheme().getBackgroundColor(), context.getColorScheme().getBackgroundColor(), IRenderer.RectMode.Fill, context);
    }

    public void renderBooleanWidget(BooleanWidget widget, Context context, Rect rect, MouseHelper mouse) {
        Color c = widget.getValue().booleanValue() ? context.getColorScheme().getMainColor(rect.getY()) : context.getColorScheme().getTertiaryBackgroundColor();

        context.getRenderer().renderRect(rect, c, c, IRenderer.RectMode.Fill, context);
        if (widget.getValue().booleanValue()) {
            context.getRenderer().renderRect(rect, context.getColorScheme().getOutlineColor(), context.getColorScheme().getOutlineColor(), IRenderer.RectMode.Outline, context);
        }

        int centerY = (rect.getHeight() - context.getRenderer().getTextHeight(widget.getTitle())) / 2;

        context.getRenderer().renderText(widget.getTitle(), (float) (rect.getX() + 2), (float) (rect.getY() + centerY), context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
    }

    public void renderBindWidget(BindWidget widget, Context context, Rect rect, MouseHelper mouse) {
        context.getRenderer().renderRect(rect, context.getColorScheme().getTertiaryBackgroundColor(), context.getColorScheme().getTertiaryBackgroundColor(), IRenderer.RectMode.Fill, context);
        float centerY = (float) (rect.getHeight() - context.getRenderer().getTextHeight(widget.getTitle())) / 2.0F + 1.0F;
        String keyName = widget.getValue().getKey() != -1 ? Keyboard.getKeyName(widget.getValue().getKey()) : "NONE";
        String text = widget.isBinding() ? "Binding..." : "Bind: " + keyName;

        context.getRenderer().renderText(text, (float) (rect.getX() + 2), (float) rect.getY() + centerY, context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
    }

    public void renderFeatureButton(FeatureButton widget, Context context, Rect rect, MouseHelper mouse) {
        Color color = widget.getValue().booleanValue() ? context.getColorScheme().getMainColor(0) : context.getColorScheme().getSecondaryBackgroundColor();

        this.renderRect(rect, color, color, IRenderer.RectMode.Fill, context);
        if (widget.getValue().booleanValue()) {
            this.renderRect(rect, this.getOutlineColor(), this.getOutlineColor(), IRenderer.RectMode.Outline, context);
        }

        int centerY = (rect.getHeight() - this.getTextHeight(widget.getTitle())) / 2 + 1;

        this.renderText(widget.getTitle(), (float) (rect.getX() + 2), (float) (rect.getY() + centerY), context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
    }

    public void renderComboBox(ComboBoxWidget widget, Context context, Rect rect, MouseHelper mouseHelper) {
        this.renderRect(rect, context.getColorScheme().getTertiaryBackgroundColor(), context.getColorScheme().getTertiaryBackgroundColor(), IRenderer.RectMode.Fill, context);
        String text = widget.getTitle() + ": " + widget.getValue();
        int centerY = (rect.getHeight() - this.getTextHeight(text)) / 2 + 1;

        this.renderText(text, (float) (rect.getX() + 2), (float) (rect.getY() + centerY), context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
    }

    public void renderSliderWidget(SliderWidget widget, Context context, Rect rect, Rect sliderRect, MouseHelper mouse) {
        this.renderRect(rect, context.getColorScheme().getTertiaryBackgroundColor(), context.getColorScheme().getTertiaryBackgroundColor(), IRenderer.RectMode.Fill, context);
        this.renderRect(sliderRect, context.getColorScheme().getMainColor(0), context.getColorScheme().getMainColor(0), IRenderer.RectMode.Fill, context);
        this.renderRect(sliderRect, context.getColorScheme().getOutlineColor(), context.getColorScheme().getOutlineColor(), IRenderer.RectMode.Outline, context);
        DecimalFormat df = new DecimalFormat("#.##");

        df.setRoundingMode(RoundingMode.CEILING);
        String text = widget.getTitle() + ": " + df.format(widget.getValue().doubleValue());
        int centerY = (rect.getHeight() - this.getTextHeight(text)) / 2 + 1;

        this.renderText(text, (float) (rect.getX() + 2), (float) (rect.getY() + centerY), context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
    }

    public void renderColorWidget(ColorWidget widget, Context context, boolean open, Rect headerRect, Rect dims, Rect container, Rect alphaSlider, Rect hueSlider, Rect colorSquare) {
        byte sliderWidth = 2;

        this.renderRect(open ? dims : headerRect, context.getColorScheme().getTertiaryBackgroundColor(), context.getColorScheme().getTertiaryBackgroundColor(), IRenderer.RectMode.Fill, context);
        String text = widget.getTitle();
        int centerY = (headerRect.getHeight() - this.getTextHeight(text)) / 2 + 1;
        String openString = open ? "-" : "+";
        int rightX = headerRect.getWidth() - this.getTextWidth(openString) - 2;

        this.renderText(text, (float) (headerRect.getX() + 2), (float) (headerRect.getY() + centerY), context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
        this.renderText(openString, (float) (headerRect.getX() + rightX), (float) (headerRect.getY() + centerY), widget.getValue(), context.getColorScheme().doesTextShadow());
        if (open) {
            byte hueSegments = 30;

            GL11.glShadeModel(7425);
            GlStateManager.disableTexture2D();
            GL11.glLineWidth((float) (hueSlider.getHeight() * 2));
            GL11.glBegin(3);

            for (int hsb = 0; hsb <= hueSegments; ++hsb) {
                double hueOffset = MathUtil.normalize((double) hsb, 0.0D, (double) hueSegments);
                Color pickerSize = Color.getHSBColor((float) hueOffset, 1.0F, 1.0F);

                ColorUtil.glColor(pickerSize);
                GL11.glVertex2f((float) ((double) hueSlider.getX() + hueOffset * (double) hueSlider.getWidth()), (float) hueSlider.getY() + (float) hueSlider.getHeight() / 2.0F);
            }

            GL11.glEnd();
            GlStateManager.enableTexture2D();
            GL11.glLineWidth(1.0F);
            float[] afloat = Color.RGBtoHSB(widget.getValue().getRed(), widget.getValue().getGreen(), widget.getValue().getBlue(), (float[]) null);
            int i = (int) (afloat[0] * (float) hueSlider.getWidth());

            i = MathHelper.clamp(i, 0, hueSlider.getWidth());
            Rect huePicker = new Rect(hueSlider.getX() + i - sliderWidth / 2, hueSlider.getY(), sliderWidth, hueSlider.getHeight());

            this.renderRect(huePicker, new Color(255, 255, 255), new Color(255, 255, 255), IRenderer.RectMode.Fill, context);
            GlStateManager.enableBlend();
            GL11.glBlendFunc(770, 771);
            GlStateManager.disableTexture2D();
            GL11.glShadeModel(7425);
            GL11.glBegin(7);
            ColorUtil.glColor(new Color(255, 255, 255));
            GL11.glVertex2f((float) colorSquare.getX(), (float) colorSquare.getY());
            ColorUtil.glColor(new Color(255, 255, 255));
            GL11.glVertex2f((float) colorSquare.getX(), (float) (colorSquare.getY() + colorSquare.getHeight()));
            ColorUtil.glColor(Color.getHSBColor(afloat[0], 1.0F, 1.0F));
            GL11.glVertex2f((float) (colorSquare.getX() + colorSquare.getWidth()), (float) (colorSquare.getY() + colorSquare.getHeight()));
            ColorUtil.glColor(Color.getHSBColor(afloat[0], 1.0F, 1.0F));
            GL11.glVertex2f((float) (colorSquare.getX() + colorSquare.getWidth()), (float) colorSquare.getY());
            GL11.glEnd();
            GL11.glBegin(7);
            ColorUtil.glColor(new Color(0, 0, 0, 24));
            GL11.glVertex2f((float) colorSquare.getX(), (float) colorSquare.getY());
            ColorUtil.glColor(new Color(0, 0, 0, 255));
            GL11.glVertex2f((float) colorSquare.getX(), (float) (colorSquare.getY() + colorSquare.getHeight()));
            ColorUtil.glColor(new Color(0, 0, 0, 255));
            GL11.glVertex2f((float) (colorSquare.getX() + colorSquare.getWidth()), (float) (colorSquare.getY() + colorSquare.getHeight()));
            ColorUtil.glColor(new Color(0, 0, 0, 24));
            GL11.glVertex2f((float) (colorSquare.getX() + colorSquare.getWidth()), (float) colorSquare.getY());
            GL11.glEnd();
            GlStateManager.enableTexture2D();
            byte b0 = 2;
            int pickerOffsetX = MathHelper.clamp((int) (afloat[1] * (float) colorSquare.getWidth()), 0, colorSquare.getWidth());
            int pickerOffsetY = MathHelper.clamp((int) (afloat[2] * (float) colorSquare.getHeight()), 0, colorSquare.getHeight());
            Rect pickerRect = new Rect(colorSquare.getX() + pickerOffsetX - b0 / 2, colorSquare.getY() + colorSquare.getHeight() - pickerOffsetY - b0 / 2, b0, b0);

            this.renderRect(pickerRect, new Color(255, 255, 255), new Color(255, 255, 255), IRenderer.RectMode.Fill, context);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glBegin(7);
            ColorUtil.glColor(ColorUtil.newAlpha(widget.getValue(), 24));
            GL11.glVertex2f((float) alphaSlider.getX(), (float) alphaSlider.getY());
            ColorUtil.glColor(ColorUtil.newAlpha(widget.getValue(), 255));
            GL11.glVertex2f((float) alphaSlider.getX(), (float) (alphaSlider.getY() + alphaSlider.getHeight()));
            ColorUtil.glColor(ColorUtil.newAlpha(widget.getValue(), 255));
            GL11.glVertex2f((float) (alphaSlider.getX() + alphaSlider.getWidth()), (float) (alphaSlider.getY() + alphaSlider.getHeight()));
            ColorUtil.glColor(ColorUtil.newAlpha(widget.getValue(), 24));
            GL11.glVertex2f((float) (alphaSlider.getX() + alphaSlider.getWidth()), (float) alphaSlider.getY());
            GL11.glEnd();
            GlStateManager.enableTexture2D();
            double alphaNormal = MathHelper.clamp(MathUtil.normalize((double) widget.getValue().getAlpha(), 0.0D, 255.0D), 0.0D, 1.0D);
            int alphaOffset = (int) (alphaNormal * (double) alphaSlider.getHeight());
            Rect alphaPicker = new Rect(alphaSlider.getX(), alphaSlider.getY() + alphaOffset - sliderWidth / 2, alphaSlider.getWidth(), sliderWidth);

            this.renderRect(alphaPicker, new Color(255, 255, 255), new Color(255, 255, 255), IRenderer.RectMode.Fill, context);
        }

    }

    public void renderStringWidget(TextEntryWidget widget, Context context, Rect rect, MouseHelper mouse) {
        this.renderRect(rect, context.getColorScheme().getTertiaryBackgroundColor(), context.getColorScheme().getTertiaryBackgroundColor(), IRenderer.RectMode.Fill, context);
        String renderText = widget.typing ? widget.getValue() + "|" : widget.getValue();
        int centerY = (rect.getHeight() - this.getTextHeight(renderText)) / 2;

        this.renderText(renderText, (float) (rect.getX() + 2), (float) (rect.getY() + centerY + 1), context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
    }

    public int getTextWidth(String text) {
        return FontModule.INSTANCE.isEnabled() ? SecondGuiModule.fontRenderer.getStringWidth(text) : SecondGuiModule.mc.fontRenderer.getStringWidth(text);
    }

    public int getTextHeight(String text) {
        return FontModule.INSTANCE.isEnabled() ? SecondGuiModule.fontRenderer.getStringHeight(text) + 1 : SecondGuiModule.mc.fontRenderer.FONT_HEIGHT;
    }

    public void renderText(String text, float x, float y, Color color, boolean shadow) {
        if (FontModule.INSTANCE.isEnabled()) {
            if (SecondGuiModule.fontRenderer.getFont().getSize() != ((Number) FontModule.INSTANCE.fontSize.getValue()).intValue()) {
                SecondGuiModule.fontRenderer.setFont(SecondGuiModule.fontRenderer.getFont().deriveFont((float) ((Number) FontModule.INSTANCE.fontSize.getValue()).intValue()));
            }

            if (shadow) {
                SecondGuiModule.fontRenderer.drawStringWithShadow(text, (double) x, (double) y, color.getRGB());
            } else {
                SecondGuiModule.fontRenderer.drawString(text, x, y, color.getRGB());
            }
        } else if (shadow) {
            SecondGuiModule.mc.fontRenderer.drawStringWithShadow(text, x, y, color.getRGB());
        } else {
            SecondGuiModule.mc.fontRenderer.drawString(text, (int) x, (int) y, color.getRGB());
        }

    }

    public void renderRect(Rect rect, Color color, Color bottom, IRenderer.RectMode mode, Context context) {
        if (mode == IRenderer.RectMode.Fill) {
            GlStateManager.enableBlend();
            GL11.glShadeModel(7425);
            GlStateManager.disableTexture2D();
            GL11.glBegin(7);
            ColorUtil.glColor(bottom);
            GL11.glVertex2f((float) rect.getX(), (float) (rect.getY() + rect.getHeight()));
            GL11.glVertex2f((float) (rect.getX() + rect.getWidth()), (float) (rect.getY() + rect.getHeight()));
            ColorUtil.glColor(color);
            GL11.glVertex2f((float) (rect.getX() + rect.getWidth()), (float) rect.getY());
            GL11.glVertex2f((float) rect.getX(), (float) rect.getY());
            GL11.glEnd();
            GlStateManager.enableTexture2D();
        }

        if (mode == IRenderer.RectMode.Outline) {
            GlStateManager.disableTexture2D();
            ColorUtil.glColor(color);
            GL11.glBegin(2);
            GL11.glVertex2f((float) rect.getX(), (float) rect.getY());
            GL11.glVertex2f((float) (rect.getX() + rect.getWidth()) + context.getMetrics().getOutlineWidth() / 2.0F, (float) rect.getY());
            GL11.glVertex2f((float) (rect.getX() + rect.getWidth()) + context.getMetrics().getOutlineWidth() / 2.0F, (float) (rect.getY() + rect.getHeight()));
            GL11.glVertex2f((float) rect.getX(), (float) (rect.getY() + rect.getHeight()));
            GL11.glEnd();
            GlStateManager.enableTexture2D();
        }

    }

    public void scissorRect(Rect dims) {
        ScaledResolution sr = new ScaledResolution(SecondGuiModule.mc);
        double factor = (double) sr.getScaleFactor();

        GL11.glScissor((int) ((double) dims.getX() * factor), sr.getScaledHeight() - (dims.getY() + dims.getHeight()), (int) ((double) dims.getWidth() * factor), (int) ((double) dims.getHeight() * factor));
    }

    public Color getMainColor(int pos) {
        return (Color) this.color.getValue();
    }

    public Color getOutlineColor() {
        return (Color) this.outlineColor.getValue();
    }

    public Color getButtonColor() {
        return (Color) this.color.getValue();
    }

    public Color getBackgroundColor() {
        return (Color) this.backgroundColor.getValue();
    }

    public Color getSecondaryBackgroundColor() {
        return (Color) this.backgroundSecondary.getValue();
    }

    public Color getTertiaryBackgroundColor() {
        return (Color) this.backgroundTertiary.getValue();
    }

    public Color getTextColor() {
        return (Color) this.textColor.getValue();
    }

    public Color getTextColorHighlight() {
        return (Color) this.textColor.getValue();
    }

    public Color getTextColorActive() {
        return (Color) this.textColor.getValue();
    }

    public boolean doesTextShadow() {
        return ((Boolean) this.textShadow.getValue()).booleanValue();
    }

    public int getSpacing() {
        return 1;
    }

    public int getBetweenSpacing() {
        return 1;
    }

    public int getSettingSpacing() {
        return 1;
    }

    public int getFrameWidth() {
        return ((Number) this.width.getValue()).intValue();
    }

    public int getButtonHeight() {
        return 14;
    }

    public int getFrameHeight() {
        return 15;
    }

    public float getOutlineWidth() {
        return 0.0F;
    }
}
