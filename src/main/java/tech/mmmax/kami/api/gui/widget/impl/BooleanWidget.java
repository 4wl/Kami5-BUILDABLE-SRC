package tech.mmmax.kami.api.gui.widget.impl;

import tech.mmmax.kami.api.gui.component.IComponent;
import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.widget.IWidget;

public class BooleanWidget implements IWidget, IComponent {

    String title;
    Rect dims;
    boolean value;

    public BooleanWidget(String title, Rect dims) {
        this.title = title;
        this.dims = dims;
        this.value = true;
    }

    public void draw(Context context, MouseHelper mouse) {
        this.getDims().setHeight(context.getMetrics().getButtonHeight());
        context.getRenderer().renderBooleanWidget(this, context, this.getDisplayDims(), mouse);
    }

    public void click(Context context, MouseHelper mouse, int button) {
        if (this.getDisplayDims().collideWithMouse(mouse) && button == 0) {
            this.setValue(Boolean.valueOf(!this.getValue().booleanValue()));
        }

    }

    public void release(Context context, MouseHelper mouse, int state) {}

    public void key(Context context, int key, char character) {}

    public int getLevel() {
        return 3;
    }

    public Rect getDims() {
        return this.dims;
    }

    public boolean isDraggable() {
        return false;
    }

    public boolean isActive() {
        return true;
    }

    public Boolean getValue() {
        return Boolean.valueOf(this.value);
    }

    public void setValue(Boolean value) {
        this.value = value.booleanValue();
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
}
