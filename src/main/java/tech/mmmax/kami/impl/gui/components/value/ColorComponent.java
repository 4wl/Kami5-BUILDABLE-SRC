package tech.mmmax.kami.impl.gui.components.value;

import java.awt.Color;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.widget.impl.ColorWidget;
import tech.mmmax.kami.api.value.Value;

public class ColorComponent extends ColorWidget {

    Value colorValue;

    public ColorComponent(Value colorValue) {
        super(colorValue.getName(), (Color) colorValue.getValue(), new Rect(0, 0, 0, 0));
        this.colorValue = colorValue;
    }

    public Color getValue() {
        return (Color) this.colorValue.getValue();
    }

    public void setValue(Color value) {
        this.colorValue.setValue(value);
    }

    public boolean isActive() {
        return this.colorValue.isActive();
    }
}
