package tech.mmmax.kami.api.gui.helpers;

public interface IDraggable {

    default void drag(Rect dims, int dragX, int dragY, MouseHelper mouse) {
        dims.setX(mouse.getX() - dragX);
        dims.setY(mouse.getY() - dragY);
    }
}
