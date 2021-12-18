package tech.mmmax.kami.api.gui.component.impl;

import tech.mmmax.kami.api.gui.component.IComponent;
import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.flow.Flow;
import tech.mmmax.kami.api.gui.flow.impl.LinearFlow;
import tech.mmmax.kami.api.gui.helpers.IDraggable;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;

public class FrameComponent implements IComponent, IDraggable {

    String titleText;
    Rect dims;
    Rect title;
    boolean open;
    boolean dragging;
    int dragX;
    int dragY;
    Flow flow;

    public FrameComponent(String title, Rect dims) {
        this.titleText = title;
        this.dims = dims;
        this.title = dims;
        this.open = true;
        this.dragging = false;
        this.flow = new LinearFlow(new Rect(0, 0, 0, 0), 1);
    }

    public String getTitleText() {
        return this.titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public void draw(Context context, MouseHelper mouse) {
        if (this.dragging) {
            this.drag(this.getDims(), this.dragX, this.dragY, mouse);
        }

        this.flow.getDims().setX(this.getDims().getX() + context.getMetrics().getSpacing());
        this.flow.getDims().setY(this.getDims().getY() + context.getMetrics().getFrameHeight() + context.getMetrics().getSpacing());
        this.flow.getDims().setWidth(this.getDims().getWidth() - context.getMetrics().getSpacing() * 2);
        this.flow.positionComponents(context);
        this.getDims().setHeight(this.title.getHeight() + this.flow.getDims().getHeight() + context.getMetrics().getSpacing() * 2);
        this.title = new Rect(this.getDims().getX(), this.getDims().getY(), this.getDims().getWidth(), context.getMetrics().getFrameHeight());
        this.getDims().setWidth(context.getMetrics().getFrameWidth());
        if (this.open) {
            context.getRenderer().renderFrame(context, this.getDims(), mouse);
        } else {
            context.getRenderer().renderFrameOutline(context, this.title, mouse);
        }

        context.getRenderer().renderFrameTitle(context, this.title, mouse, this.getTitleText(), this.open);
        if (this.open) {
            this.flow.draw(context, mouse);
            context.getRenderer().renderFrameOutline(context, this.getDims(), mouse);
        }

    }

    public void click(Context context, MouseHelper mouse, int button) {
        if (this.title.collideWithMouse(mouse)) {
            if (button == 1) {
                this.open = !this.open;
            } else if (this.isDraggable()) {
                this.dragging = true;
                this.dragX = mouse.getX() - this.getDims().getX();
                this.dragY = mouse.getY() - this.getDims().getY();
            }
        }

        if (this.open) {
            this.flow.click(context, mouse, button);
        }

    }

    public void release(Context context, MouseHelper mouse, int state) {
        this.dragging = false;
        if (this.open) {
            this.flow.release(context, mouse, state);
        }

    }

    public void key(Context context, int key, char keycode) {
        if (this.open) {
            this.flow.key(context, key, keycode);
        }

    }

    public int getLevel() {
        return 0;
    }

    public Rect getDims() {
        return this.dims;
    }

    public Flow getFlow() {
        return this.flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    public boolean isDraggable() {
        return true;
    }

    public boolean isActive() {
        return true;
    }
}
