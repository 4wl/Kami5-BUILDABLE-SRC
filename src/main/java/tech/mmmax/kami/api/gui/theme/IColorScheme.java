package tech.mmmax.kami.api.gui.theme;

import java.awt.Color;

public interface IColorScheme {

    Color getMainColor(int i);

    Color getOutlineColor();

    Color getButtonColor();

    Color getBackgroundColor();

    Color getSecondaryBackgroundColor();

    Color getTertiaryBackgroundColor();

    Color getTextColor();

    Color getTextColorHighlight();

    Color getTextColorActive();

    boolean doesTextShadow();
}
