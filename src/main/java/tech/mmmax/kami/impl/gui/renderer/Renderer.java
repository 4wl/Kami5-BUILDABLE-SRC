package tech.mmmax.kami.impl.gui.renderer;

import java.awt.Color;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.render.IRenderer;
import tech.mmmax.kami.api.gui.widget.impl.BindWidget;
import tech.mmmax.kami.api.gui.widget.impl.BooleanWidget;
import tech.mmmax.kami.api.gui.widget.impl.ColorWidget;
import tech.mmmax.kami.api.gui.widget.impl.ComboBoxWidget;
import tech.mmmax.kami.api.gui.widget.impl.SliderWidget;
import tech.mmmax.kami.api.gui.widget.impl.TextEntryWidget;
import tech.mmmax.kami.api.utils.MathUtil;
import tech.mmmax.kami.api.utils.color.ColorUtil;
import tech.mmmax.kami.api.utils.render.font.CFontRenderer;
import tech.mmmax.kami.api.wrapper.IMinecraft;
import tech.mmmax.kami.impl.features.modules.client.ClickGuiModule;
import tech.mmmax.kami.impl.features.modules.client.FontModule;
import tech.mmmax.kami.impl.gui.components.module.FeatureButton;

public class Renderer implements IRenderer, IMinecraft {

    static CFontRenderer fontRenderer = new CFontRenderer(FontModule.INSTANCE.font);

    public void preRender(Context context) {}

    public void renderFrameTitle(Context context, Rect rect, MouseHelper mouse, String title, boolean open) {
        this.renderRect(rect, context.getColorScheme().getMainColor(rect.getX() + rect.getY()), context.getColorScheme().getMainColor(rect.getX() + rect.getWidth() + rect.getY() + rect.getHeight()), IRenderer.RectMode.Fill, context);
        int centerX = (rect.getWidth() - this.getTextWidth(title)) / 2;
        int centerY = (rect.getHeight() - this.getTextHeight(title)) / 2 + 1;
        String openString = open ? "-" : "+";
        int rightX = rect.getWidth() - this.getTextWidth(openString) - 2;

        this.renderText(title, (float) (rect.getX() + centerX), (float) (rect.getY() + centerY), context.getColorScheme().getTextColorHighlight(), context.getColorScheme().doesTextShadow());
        this.renderText(openString, (float) (rect.getX() + rightX), (float) (rect.getY() + centerY), context.getColorScheme().getTextColorHighlight(), context.getColorScheme().doesTextShadow());
    }

    public void renderFrameOutline(Context context, Rect rect, MouseHelper mouse) {
        this.renderRect(rect, ClickGuiModule.INSTANCE.getOutlineColor(), ClickGuiModule.INSTANCE.getOutlineColor(), IRenderer.RectMode.Outline, context);
    }

    public void renderFrame(Context context, Rect rect, MouseHelper mouse) {
        this.renderRect(rect, context.getColorScheme().getBackgroundColor(), context.getColorScheme().getBackgroundColor(), IRenderer.RectMode.Fill, context);
    }

    public void renderBooleanWidget(BooleanWidget widget, Context context, Rect rect, MouseHelper mouse) {
        Color color = widget.getValue().booleanValue() ? context.getColorScheme().getMainColor(rect.getX() + rect.getY()) : context.getColorScheme().getTertiaryBackgroundColor();
        Color color2 = widget.getValue().booleanValue() ? context.getColorScheme().getMainColor(rect.getX() + rect.getWidth() + rect.getY() + rect.getHeight()) : context.getColorScheme().getTertiaryBackgroundColor();

        this.renderRect(rect, color, color2, IRenderer.RectMode.Fill, context);
        int centerX = (rect.getWidth() - this.getTextWidth(widget.getTitle())) / 2;
        int centerY = (rect.getHeight() - this.getTextHeight(widget.getTitle())) / 2 + 1;

        this.renderText(widget.getTitle(), (float) (rect.getX() + centerX), (float) (rect.getY() + centerY), context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
    }

    public void renderBindWidget(BindWidget widget, Context context, Rect rect, MouseHelper mouse) {
        this.renderRect(rect, context.getColorScheme().getTertiaryBackgroundColor(), context.getColorScheme().getTertiaryBackgroundColor(), IRenderer.RectMode.Fill, context);
        float centerY = (float) (rect.getHeight() - this.getTextHeight(widget.getTitle())) / 2.0F + 1.0F;
        String keyName = widget.getValue().getKey() != -1 ? Keyboard.getKeyName(widget.getValue().getKey()) : "NONE";
        String text = widget.isBinding() ? "Binding..." : "Bind: " + keyName;

        this.renderText(text, (float) (rect.getX() + 2), (float) rect.getY() + centerY, context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
    }

    public void renderFeatureButton(FeatureButton widget, Context context, Rect rect, MouseHelper mouse) {
        Color color = widget.getValue().booleanValue() ? context.getColorScheme().getMainColor(0) : context.getColorScheme().getSecondaryBackgroundColor();

        this.renderRect(rect, color, color, IRenderer.RectMode.Fill, context);
        int centerX = (rect.getWidth() - this.getTextWidth(widget.getTitle())) / 2;
        int centerY = (rect.getHeight() - this.getTextHeight(widget.getTitle())) / 2 + 1;

        this.renderText(widget.getTitle(), (float) (rect.getX() + centerX), (float) (rect.getY() + centerY), context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
    }

    public void renderComboBox(ComboBoxWidget widget, Context context, Rect rect, MouseHelper mouseHelper) {
        this.renderRect(rect, context.getColorScheme().getTertiaryBackgroundColor(), context.getColorScheme().getTertiaryBackgroundColor(), IRenderer.RectMode.Fill, context);
        String text = widget.getTitle() + ": " + widget.getValue();
        int centerX = (rect.getWidth() - this.getTextWidth(text)) / 2;
        int centerY = (rect.getHeight() - this.getTextHeight(text)) / 2 + 1;

        this.renderText(text, (float) (rect.getX() + centerX), (float) (rect.getY() + centerY), context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
    }

    public void renderSliderWidget(SliderWidget widget, Context context, Rect rect, Rect sliderRect, MouseHelper mouse) {
        this.renderRect(rect, context.getColorScheme().getTertiaryBackgroundColor(), context.getColorScheme().getTertiaryBackgroundColor(), IRenderer.RectMode.Fill, context);
        this.renderRect(sliderRect, context.getColorScheme().getMainColor(0), context.getColorScheme().getMainColor(0), IRenderer.RectMode.Fill, context);
        DecimalFormat df = new DecimalFormat("#.##");

        df.setRoundingMode(RoundingMode.CEILING);
        String text = widget.getTitle() + ": " + df.format(widget.getValue().doubleValue());
        int centerX = (rect.getWidth() - this.getTextWidth(text)) / 2;
        int centerY = (rect.getHeight() - this.getTextHeight(text)) / 2 + 1;

        this.renderText(text, (float) (rect.getX() + centerX), (float) (rect.getY() + centerY), context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
    }

    public void renderColorWidget(ColorWidget widget, Context context, boolean open, Rect headerRect, Rect dims, Rect container, Rect alphaSlider, Rect hueSlider, Rect colorSquare) {
        byte sliderWidth = 2;

        this.renderRect(open ? dims : headerRect, context.getColorScheme().getTertiaryBackgroundColor(), context.getColorScheme().getTertiaryBackgroundColor(), IRenderer.RectMode.Fill, context);
        String text = widget.getTitle();
        int centerX = (headerRect.getWidth() - this.getTextWidth(text)) / 2;
        int centerY = (headerRect.getHeight() - this.getTextHeight(text)) / 2 + 1;
        String openString = open ? "-" : "+";
        int rightX = headerRect.getWidth() - this.getTextWidth(openString) - 2;

        this.renderText(text, (float) (headerRect.getX() + centerX), (float) (headerRect.getY() + centerY), context.getColorScheme().getTextColor(), context.getColorScheme().doesTextShadow());
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
        return FontModule.INSTANCE.isEnabled() ? Renderer.fontRenderer.getStringWidth(text) : Renderer.mc.fontRenderer.getStringWidth(text);
    }

    public int getTextHeight(String text) {
        return FontModule.INSTANCE.isEnabled() ? Renderer.fontRenderer.getStringHeight(text) + 1 : Renderer.mc.fontRenderer.FONT_HEIGHT;
    }

    public void renderText(String text, float x, float y, Color color, boolean shadow) {
        if (FontModule.INSTANCE.isEnabled()) {
            if (Renderer.fontRenderer.getFont().getSize() != ((Number) FontModule.INSTANCE.fontSize.getValue()).intValue()) {
                Renderer.fontRenderer.setFont(Renderer.fontRenderer.getFont().deriveFont((float) ((Number) FontModule.INSTANCE.fontSize.getValue()).intValue()));
            }

            if (shadow) {
                Renderer.fontRenderer.drawStringWithShadow(text, (double) x, (double) y, color.getRGB());
            } else {
                Renderer.fontRenderer.drawString(text, x, y, color.getRGB());
            }
        } else if (shadow) {
            Renderer.mc.fontRenderer.drawStringWithShadow(text, x, y, color.getRGB());
        } else {
            Renderer.mc.fontRenderer.drawString(text, (int) x, (int) y, color.getRGB());
        }

    }

    public void renderRect(Rect rect, Color color, Color bottom, IRenderer.RectMode mode, Context context) {
        if (mode == IRenderer.RectMode.Fill) {
            ColorUtil.glColor(color);
            Gui.drawRect(rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), color.getRGB());
        }

        if (mode == IRenderer.RectMode.Outline) {
            GlStateManager.disableTexture2D();
            float red = (float) color.getRed() / 255.0F;
            float green = (float) color.getGreen() / 255.0F;
            float blue = (float) color.getBlue() / 255.0F;
            float alpha = (float) color.getAlpha() / 255.0F;

            GL11.glLineWidth(context.getMetrics().getOutlineWidth());
            GL11.glColor4f(red, green, blue, alpha);
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
        ScaledResolution sr = new ScaledResolution(Renderer.mc);
        double factor = (double) sr.getScaleFactor();

        GL11.glScissor((int) ((double) dims.getX() * factor), sr.getScaledHeight() - (dims.getY() + dims.getHeight()), (int) ((double) dims.getWidth() * factor), (int) ((double) dims.getHeight() * factor));
    }
}
