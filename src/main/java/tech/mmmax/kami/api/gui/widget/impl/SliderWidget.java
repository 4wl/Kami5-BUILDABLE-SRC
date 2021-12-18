package tech.mmmax.kami.api.gui.widget.impl;

import net.minecraft.util.math.MathHelper;
import tech.mmmax.kami.api.gui.component.IComponent;
import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.widget.IWidget;

public class SliderWidget implements IWidget, IComponent {

    String title;
    Number value;
    public Number min;
    public Number max;
    boolean dragging;
    Rect dims;
    Rect slider;

    public SliderWidget(String title, Number number, Number min, Number max) {
        this.title = title;
        this.value = number;
        this.min = min;
        this.max = max;
        this.dims = new Rect(0, 0, 0, 0);
        this.slider = new Rect(0, 0, 0, 0);
    }

    public Number getValue() {
        return this.value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Rect getDisplayDims() {
        return this.getDims();
    }

    public void draw(Context context, MouseHelper mouse) {
        this.getDims().setHeight(context.getMetrics().getButtonHeight());
        this.slider.setHeight(this.getDims().getHeight());
        this.slider.setX(this.getDims().getX());
        this.slider.setY(this.getDims().getY());
        double sliderWidth = this.normalize(this.getValue().doubleValue(), this.min.doubleValue(), this.max.doubleValue()) * (double) this.getDims().getWidth();

        this.slider.setWidth((int) sliderWidth);
        if (this.dragging) {
            Integer newVal = Integer.valueOf(0);
            Double difference = Double.valueOf(this.max.doubleValue() - this.min.doubleValue());
            Double newVal1 = Double.valueOf(this.min.doubleValue() + MathHelper.clamp(this.normalize((double) (mouse.getX() - this.getDims().getX()), 0.0D, (double) this.getDims().getWidth()), 0.0D, 1.0D) * difference.doubleValue());

            this.setValue((Number) newVal1);
        }

        context.getRenderer().renderSliderWidget(this, context, this.getDisplayDims(), this.slider, mouse);
    }

    public void click(Context context, MouseHelper mouse, int button) {
        if (this.getDims().collideWithMouse(mouse) && button == 0) {
            this.dragging = true;
        }

    }

    public void release(Context context, MouseHelper mouse, int state) {
        this.dragging = false;
    }

    public void key(Context context, int key, char character) {}

    public Rect getDims() {
        return this.dims;
    }

    public boolean isDraggable() {
        return false;
    }

    public boolean isActive() {
        return true;
    }

    double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    public int getLevel() {
        return 3;
    }
}
