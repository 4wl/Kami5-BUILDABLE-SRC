package tech.mmmax.kami.api.gui.widget;

import tech.mmmax.kami.api.gui.helpers.Rect;

public interface IWidget {

    Object getValue();

    void setValue(Object object);

    String getTitle();

    void setTitle(String s);

    Rect getDisplayDims();
}
