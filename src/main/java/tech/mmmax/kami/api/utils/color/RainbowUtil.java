package tech.mmmax.kami.api.utils.color;

import java.awt.Color;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class RainbowUtil {

    public static RainbowUtil INSTANCE;
    Color color = new Color(0, 0, 0);
    Color start = new Color(0, 0, 0);
    Color end = new Color(0, 0, 0);
    RainbowUtil.Modes mode;
    float hue;

    public RainbowUtil() {
        this.mode = RainbowUtil.Modes.Rainbow;
        this.hue = 0.0F;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (this.mode == RainbowUtil.Modes.Rainbow) {
            ++this.hue;
            if (this.hue >= 360.0F) {
                this.hue = 0.0F;
            }
        }

    }

    public Color getColor(float sat, float bri) {
        return Color.getHSBColor(this.hue / 360.0F, sat, bri);
    }

    public Color getStart() {
        return this.start;
    }

    public Color getEnd() {
        return this.end;
    }

    public void setStart(Color start) {
        this.start = start;
    }

    public void setEnd(Color end) {
        this.end = end;
    }

    public RainbowUtil.Modes getMode() {
        return this.mode;
    }

    public void setMode(RainbowUtil.Modes mode) {
        this.mode = mode;
    }

    static enum Modes {

        SinWave, Rainbow;
    }
}
