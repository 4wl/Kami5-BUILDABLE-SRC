package tech.mmmax.kami.api.gui.widget.impl;

import java.awt.Color;
import net.minecraft.util.math.MathHelper;
import tech.mmmax.kami.api.gui.component.IComponent;
import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.widget.IWidget;
import tech.mmmax.kami.api.utils.MathUtil;
import tech.mmmax.kami.api.utils.color.ColorUtil;

public class ColorWidget implements IWidget, IComponent {

    Color value;
    String title;
    boolean open = false;
    Rect dims;
    Rect displayDims;
    Rect insideDims;
    Rect pickerRect;
    Rect alphaRect;
    Rect hueRect;
    Rect colorSquare;
    BooleanWidget rainbow;
    BooleanWidget sync;
    boolean draggingHue;
    boolean draggingColor;
    boolean draggingAlpha;

    public ColorWidget(String title, Color value, Rect dims) {
        this.title = title;
        this.value = value;
        this.dims = dims;
        this.displayDims = new Rect(0, 0, 0, 0);
        this.pickerRect = new Rect(0, 0, 0, 0);
        this.alphaRect = new Rect(0, 0, 0, 0);
        this.insideDims = new Rect(0, 0, 0, 0);
        this.hueRect = new Rect(0, 0, 0, 0);
        this.colorSquare = new Rect(0, 0, 0, 0);
    }

    public Color getValue() {
        return this.value;
    }

    public void setValue(Color value) {
        this.value = value;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Rect getDisplayDims() {
        return this.open ? this.dims : this.displayDims;
    }

    public void draw(Context context, MouseHelper mouse) {
        this.displayDims.setX(this.getDims().getX());
        this.displayDims.setY(this.getDims().getY());
        this.displayDims.setWidth(this.getDims().getWidth());
        this.displayDims.setHeight(context.getMetrics().getButtonHeight());
        float[] hsb = Color.RGBtoHSB(this.getValue().getRed(), this.getValue().getGreen(), this.getValue().getBlue(), (float[]) null);
        byte spacing = 1;
        int alphaSliderWidth = context.getMetrics().getButtonHeight();
        int squareSize = this.insideDims.getWidth() - (alphaSliderWidth + spacing * 3);

        if (this.open) {
            this.insideDims.setX(this.getDims().getX() + context.getMetrics().getSpacing());
            this.insideDims.setY(this.getDims().getY() + this.displayDims.getHeight() + context.getMetrics().getSpacing());
            this.insideDims.setWidth(this.getDims().getWidth() - context.getMetrics().getSpacing() * 2);
            this.insideDims.setHeight(this.getDims().getHeight() - this.displayDims.getHeight() - context.getMetrics().getSpacing() * 2);
            this.hueRect.setX(this.insideDims.getX() + spacing);
            this.hueRect.setWidth(this.insideDims.getWidth() - spacing * 2);
            this.hueRect.setY(this.insideDims.getY() + spacing + squareSize + spacing);
            this.hueRect.setHeight(context.getMetrics().getButtonHeight());
            this.alphaRect.setX(this.colorSquare.getX() + this.colorSquare.getWidth() + spacing);
            this.alphaRect.setY(this.colorSquare.getY());
            this.alphaRect.setWidth(alphaSliderWidth);
            this.alphaRect.setHeight(this.colorSquare.getHeight());
            this.colorSquare.setX(this.insideDims.getX() + spacing);
            this.colorSquare.setY(this.insideDims.getY() + spacing);
            this.colorSquare.setWidth(squareSize);
            this.colorSquare.setHeight(squareSize);
        }

        this.dims.setHeight(this.displayDims.getHeight() + squareSize + this.hueRect.getHeight() + context.getMetrics().getSpacing() * 4);
        int clampMouseY;
        float normal;

        if (this.draggingHue) {
            clampMouseY = MathHelper.clamp(mouse.getX(), this.hueRect.getX(), this.hueRect.getX() + this.hueRect.getWidth());
            normal = (float) MathUtil.normalize((double) clampMouseY, (double) this.hueRect.getX(), (double) (this.hueRect.getX() + this.hueRect.getWidth()));
            this.setValue(ColorUtil.newAlpha(Color.getHSBColor(MathHelper.clamp(normal, 0.0F, 1.0F), hsb[1], hsb[2]), this.getValue().getAlpha()));
        }

        if (this.draggingColor) {
            clampMouseY = MathHelper.clamp(mouse.getX(), this.colorSquare.getX(), this.colorSquare.getX() + this.colorSquare.getWidth());
            normal = (float) MathUtil.normalize((double) clampMouseY, (double) this.colorSquare.getX(), (double) (this.colorSquare.getX() + this.colorSquare.getWidth()));
            int clampMouseY1 = MathHelper.clamp(mouse.getY(), this.colorSquare.getY(), this.colorSquare.getY() + this.colorSquare.getHeight());
            float normalY = (float) MathUtil.normalize((double) clampMouseY1, (double) this.colorSquare.getY(), (double) (this.colorSquare.getY() + this.colorSquare.getHeight()));

            normalY = -normalY + 1.0F;
            normalY = MathHelper.clamp(normalY, 0.0F, 1.0F);
            this.setValue(ColorUtil.newAlpha(Color.getHSBColor(hsb[0], normal, normalY), this.getValue().getAlpha()));
        }

        if (this.draggingAlpha) {
            clampMouseY = MathHelper.clamp(mouse.getY(), this.alphaRect.getY(), this.alphaRect.getY() + this.alphaRect.getHeight());
            normal = (float) MathUtil.normalize((double) clampMouseY, (double) this.alphaRect.getY(), (double) (this.alphaRect.getY() + this.alphaRect.getHeight()));
            this.setValue(ColorUtil.newAlpha(this.getValue(), (int) MathHelper.clamp(normal * 255.0F, 0.0F, 255.0F)));
        }

        context.getRenderer().renderColorWidget(this, context, this.open, this.displayDims, this.dims, this.insideDims, this.alphaRect, this.hueRect, this.colorSquare);
    }

    public void click(Context context, MouseHelper mouse, int button) {
        if (this.displayDims.collideWithMouse(mouse) && button == 1) {
            this.open = !this.open;
        }

        if (this.open) {
            if (this.hueRect.collideWithMouse(mouse)) {
                this.draggingHue = true;
            }

            if (this.colorSquare.collideWithMouse(mouse)) {
                this.draggingColor = true;
            }

            if (this.alphaRect.collideWithMouse(mouse)) {
                this.draggingAlpha = true;
            }
        }

    }

    public void release(Context context, MouseHelper mouse, int state) {
        this.draggingHue = false;
        this.draggingColor = false;
        this.draggingAlpha = false;
    }

    public void key(Context context, int key, char character) {}

    public int getLevel() {
        return 3;
    }

    public Rect getDims() {
        return this.open ? this.dims : this.displayDims;
    }

    public boolean isDraggable() {
        return false;
    }

    public boolean isActive() {
        return true;
    }
}
