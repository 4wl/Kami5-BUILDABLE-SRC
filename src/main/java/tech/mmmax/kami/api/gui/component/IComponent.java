package tech.mmmax.kami.api.gui.component;

import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;

public interface IComponent {

    void draw(Context context, MouseHelper mousehelper);

    void click(Context context, MouseHelper mousehelper, int i);

    void release(Context context, MouseHelper mousehelper, int i);

    void key(Context context, int i, char c0);

    int getLevel();

    Rect getDims();

    boolean isDraggable();

    boolean isActive();
}
