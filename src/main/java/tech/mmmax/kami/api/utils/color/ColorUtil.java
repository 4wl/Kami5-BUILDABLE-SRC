package tech.mmmax.kami.api.utils.color;

import java.awt.Color;
import org.lwjgl.opengl.GL11;

public class ColorUtil {

    public static Color newAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static void glColor(Color color) {
        GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F);
    }

    public static Color interpolate(float value, Color start, Color end) {
        float sr = (float) start.getRed() / 255.0F;
        float sg = (float) start.getGreen() / 255.0F;
        float sb = (float) start.getBlue() / 255.0F;
        float sa = (float) start.getAlpha() / 255.0F;
        float er = (float) end.getRed() / 255.0F;
        float eg = (float) end.getGreen() / 255.0F;
        float eb = (float) end.getBlue() / 255.0F;
        float ea = (float) end.getAlpha() / 255.0F;
        float r = sr * value + er * (1.0F - value);
        float g = sg * value + eg * (1.0F - value);
        float b = sb * value + eb * (1.0F - value);
        float a = sa * value + ea * (1.0F - value);

        return new Color(r, g, b, a);
    }
}
