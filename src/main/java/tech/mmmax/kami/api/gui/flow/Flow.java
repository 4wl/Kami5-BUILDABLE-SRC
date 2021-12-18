package tech.mmmax.kami.api.gui.flow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import tech.mmmax.kami.api.gui.component.IComponent;
import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;

public abstract class Flow implements IComponent {

    public List components;
    public Rect dims;
    int level;

    public Flow(Rect dims, int level) {
        this.dims = dims;
        this.components = new ArrayList();
        this.level = level;
    }

    public void positionComponents(Context context) {}

    public void draw(Context context, MouseHelper mouse) {
        Iterator iterator = this.getComponents().iterator();

        while (iterator.hasNext()) {
            IComponent component = (IComponent) iterator.next();

            if (component.isActive()) {
                component.draw(context, mouse);
            }
        }

    }

    public void click(Context context, MouseHelper mouse, int button) {
        Iterator iterator = this.getComponents().iterator();

        while (iterator.hasNext()) {
            IComponent component = (IComponent) iterator.next();

            if (component.isActive()) {
                component.click(context, mouse, button);
            }
        }

    }

    public void release(Context context, MouseHelper mouse, int state) {
        Iterator iterator = this.getComponents().iterator();

        while (iterator.hasNext()) {
            IComponent component = (IComponent) iterator.next();

            if (component.isActive()) {
                component.release(context, mouse, state);
            }
        }

    }

    public void key(Context context, int key, char character) {
        Iterator iterator = this.getComponents().iterator();

        while (iterator.hasNext()) {
            IComponent component = (IComponent) iterator.next();

            if (component.isActive()) {
                component.key(context, key, character);
            }
        }

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

    public List getComponents() {
        return this.components;
    }

    public int getLevel() {
        return this.level;
    }
}
