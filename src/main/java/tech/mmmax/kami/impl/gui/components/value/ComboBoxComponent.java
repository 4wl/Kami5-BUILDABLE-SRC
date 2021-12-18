package tech.mmmax.kami.impl.gui.components.value;

import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.widget.impl.ComboBoxWidget;
import tech.mmmax.kami.api.value.Value;

public class ComboBoxComponent extends ComboBoxWidget {

    Value value;

    public ComboBoxComponent(Value value) {
        super(value.getName(), (String) value.getValue(), value.getModes(), new Rect(0, 0, 0, 0));
        this.value = value;
    }

    public void draw(Context context, MouseHelper mouse) {
        this.setValue((String) this.value.getValue());
        super.draw(context, mouse);
    }

    public void click(Context context, MouseHelper mouse, int button) {
        super.click(context, mouse, button);
    }

    public boolean isActive() {
        return this.value.isActive();
    }

    public void setValue(String value) {
        super.setValue(value);
        this.value.setValue(value);
    }
}
