package tech.mmmax.kami.api.gui.widget.impl;

import net.minecraft.util.ChatAllowedCharacters;
import tech.mmmax.kami.api.gui.component.IComponent;
import tech.mmmax.kami.api.gui.context.Context;
import tech.mmmax.kami.api.gui.helpers.MouseHelper;
import tech.mmmax.kami.api.gui.helpers.Rect;
import tech.mmmax.kami.api.gui.widget.IWidget;

public class TextEntryWidget implements IWidget, IComponent {

    Rect dims;
    String value;
    public boolean typing = false;

    public TextEntryWidget(Rect dims, String value) {
        this.dims = dims;
        this.value = value;
    }

    public void draw(Context context, MouseHelper mouse) {
        if (this.typing && !this.getDims().collideWithMouse(mouse)) {
            this.typing = false;
        }

        this.getDims().setHeight(context.getMetrics().getButtonHeight());
        context.getRenderer().renderStringWidget(this, context, this.getDims(), mouse);
    }

    public void click(Context context, MouseHelper mouse, int button) {
        if (this.getDims().collideWithMouse(mouse)) {
            this.typing = !this.typing;
        }

    }

    public void release(Context context, MouseHelper mouse, int state) {}

    public void key(Context context, int key, char character) {
        if (this.typing) {
            if (key == 28) {
                this.typing = false;
                return;
            }

            if (key == 14) {
                try {
                    this.setValue(this.getValue().substring(0, this.getValue().length() - 1));
                } catch (Exception exception) {
                    ;
                }

                return;
            }

            if (ChatAllowedCharacters.isAllowedCharacter(character)) {
                this.setValue(this.getValue() + character);
                return;
            }
        }

    }

    public int getLevel() {
        return 3;
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

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return "";
    }

    public void setTitle(String title) {}

    public Rect getDisplayDims() {
        return this.getDims();
    }
}
