package tech.mmmax.kami.impl.gui.components.value;

import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.widget.impl.TextEntryWidget;
import tech.mmmax.kami.api.value.Value;

public class StringComponent extends TextEntryWidget {

    Value stringValue;

    public StringComponent(Value stringValue) {
        super(new Rect(0, 0, 0, 0), (String) stringValue.getValue());
        this.stringValue = stringValue;
    }

    public String getValue() {
        return (String) this.stringValue.getValue();
    }

    public void setValue(String value) {
        this.stringValue.setValue(value);
        super.setValue(value);
    }

    public boolean isActive() {
        return this.stringValue.isActive();
    }
}
