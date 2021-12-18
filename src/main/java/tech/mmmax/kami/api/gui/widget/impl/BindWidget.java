package tech.mmmax.kami.api.gui.widget.impl;

import tech.mmmax.kami.api.gui.component.IComponent;
import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.widget.IWidget;
import tech.mmmax.kami.api.value.custom.Bind;

public class BindWidget implements IWidget, IComponent {

    Bind value;
    String title;
    Rect dims;
    boolean binding;

    public BindWidget(String title, Bind value) {
        this.title = title;
        this.value = value;
        this.dims = new Rect(0, 0, 0, 0);
    }

    public Bind getValue() {
        return this.value;
    }

    public void setValue(Bind value) {
        this.value = value;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Rect getDisplayDims() {
        return this.dims;
    }

    public void draw(Context context, MouseHelper mouse) {
        this.getDims().setHeight(context.getMetrics().getButtonHeight());
        context.getRenderer().renderBindWidget(this, context, this.getDisplayDims(), mouse);
    }

    public void click(Context context, MouseHelper mouse, int button) {
        if (this.getDims().collideWithMouse(mouse)) {
            if (button == 0) {
                this.binding = true;
            }

            if (button == 1) {
                this.getValue().setKey(-1);
            }
        }

    }

    public void release(Context context, MouseHelper mouse, int state) {}

    public void key(Context context, int key, char character) {
        if (this.binding) {
            if (key != 1) {
                this.getValue().setKey(key);
            }

            this.binding = false;
        }

    }

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

    public boolean isBinding() {
        return this.binding;
    }

    public void setBinding(boolean binding) {
        this.binding = binding;
    }
}
