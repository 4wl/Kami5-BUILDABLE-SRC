package tech.mmmax.kami.impl.gui.components.value;

import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.widget.impl.SliderWidget;
import tech.mmmax.kami.api.value.Value;

public class SliderComponent extends SliderWidget {

    Value val;

    public SliderComponent(Value value) {
        super(value.getName(), (Number) value.getValue(), (Number) value.getMin(), (Number) value.getMax());
        this.val = value;
    }

    public void draw(Context context, MouseHelper mouse) {
        super.draw(context, mouse);
        this.setValue((Number) this.val.getValue());
        this.min = (Number) this.val.getMin();
        this.max = (Number) this.val.getMax();
    }

    public void setValue(Number value) {
        super.setValue(value);
        this.val.setValue(value);
    }

    public boolean isActive() {
        return this.val.isActive();
    }
}
