package tech.mmmax.kami.impl.gui.components.value;

import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.widget.impl.BooleanWidget;
import tech.mmmax.kami.api.value.Value;

public class BooleanComponent extends BooleanWidget {

    Value value;

    public BooleanComponent(Value booleanValue) {
        super(booleanValue.getName(), new Rect(0, 0, 0, 0));
        this.value = booleanValue;
    }

    public void draw(Context context, MouseHelper mouse) {
        this.setValue((Boolean) this.value.getValue());
        super.draw(context, mouse);
    }

    public void setValue(Boolean value) {
        super.setValue(value);
        this.value.setValue(value);
    }

    public boolean isActive() {
        return this.value.isActive();
    }
}
