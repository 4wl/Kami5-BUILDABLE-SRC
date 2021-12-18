package tech.mmmax.kami.impl.gui.components.module;

import java.awt.Color;
import java.util.Iterator;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.flow.Flow;
import tech.mmmax.kami.api.gui.flow.impl.LinearFlow;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.widget.impl.BindWidget;
import tech.mmmax.kami.api.gui.widget.impl.BooleanWidget;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.impl.gui.components.value.BooleanComponent;
import tech.mmmax.kami.impl.gui.components.value.ColorComponent;
import tech.mmmax.kami.impl.gui.components.value.ComboBoxComponent;
import tech.mmmax.kami.impl.gui.components.value.SliderComponent;
import tech.mmmax.kami.impl.gui.components.value.StringComponent;

public class FeatureButton extends BooleanWidget {

    Feature feature;
    boolean open;
    Rect header;
    Flow flow;

    public FeatureButton(Feature feature, Rect dims) {
        super(feature.getName(), dims);
        this.feature = feature;
        this.open = false;
        this.header = new Rect(0, 0, 0, 0);
        this.flow = new LinearFlow(new Rect(0, 0, 0, 0), 2);
        if (feature instanceof Module) {
            this.flow.getComponents().add(new BindWidget("Module Bind", ((Module) feature).getBind()));
        }

        Iterator iterator = feature.getValues().iterator();

        while (iterator.hasNext()) {
            Value value = (Value) iterator.next();

            if (value.getValue() instanceof Boolean) {
                this.flow.getComponents().add(new BooleanComponent(value));
            }

            if (value.getValue() instanceof String) {
                if (value.getModes() != null) {
                    this.flow.getComponents().add(new ComboBoxComponent(value));
                } else {
                    this.flow.getComponents().add(new StringComponent(value));
                }
            }

            if (value.getValue() instanceof Number) {
                this.flow.getComponents().add(new SliderComponent(value));
            }

            if (value.getValue() instanceof Color) {
                this.flow.getComponents().add(new ColorComponent(value));
            }
        }

    }

    public void draw(Context context, MouseHelper mouse) {
        this.setValue(Boolean.valueOf(this.feature.isEnabled()));
        this.header.setX(this.getDims().getX());
        this.header.setY(this.getDims().getY());
        this.header.setWidth(this.getDims().getWidth());
        this.header.setHeight(context.getMetrics().getButtonHeight());
        this.flow.getDims().setX(this.getDims().getX() + context.getMetrics().getSpacing());
        this.flow.getDims().setY(this.getDims().getY() + this.header.getHeight() + context.getMetrics().getBetweenSpacing());
        this.flow.getDims().setWidth(this.getDims().getWidth() - context.getMetrics().getSpacing() * 2);
        this.flow.positionComponents(context);
        this.getDims().setHeight(context.getMetrics().getButtonHeight());
        context.getRenderer().renderFeatureButton(this, context, this.getDisplayDims(), mouse);
        if (this.open) {
            this.getDims().setHeight(this.header.getHeight() + this.flow.getDims().getHeight());
            this.flow.draw(context, mouse);
        }

    }

    public void click(Context context, MouseHelper mouse, int button) {
        super.click(context, mouse, button);
        if (this.getDisplayDims().collideWithMouse(mouse)) {
            if (button == 0) {
                this.feature.setEnabled(this.getValue().booleanValue());
            }

            if (button == 1) {
                this.open = !this.open;
            }
        }

        if (this.open) {
            this.flow.click(context, mouse, button);
        }

    }

    public void release(Context context, MouseHelper mouse, int state) {
        super.release(context, mouse, state);
        if (this.open) {
            this.flow.release(context, mouse, state);
        }

    }

    public void key(Context context, int key, char character) {
        super.key(context, key, character);
        if (this.open) {
            this.flow.key(context, key, character);
        }

    }

    public Rect getDims() {
        return super.getDims();
    }

    public Rect getDisplayDims() {
        return this.header;
    }
}
