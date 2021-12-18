package tech.mmmax.kami.impl.features.hud;

import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import tech.mmmax.kami.api.feature.hud.HudComponent;
import tech.mmmax.kami.impl.KamiMod;
import tech.mmmax.kami.impl.features.modules.client.HudColors;
import tech.mmmax.kami.impl.gui.ClickGui;

public class Watermark extends HudComponent {

    public Watermark() {
        super("Watermark");
    }

    public void draw(Text event) {
        super.draw(event);
        ClickGui.CONTEXT.getRenderer().renderText(KamiMod.NAME_VERSION_COLORED, ((Number) this.xPos.getValue()).floatValue(), ((Number) this.yPos.getValue()).floatValue(), HudColors.getTextColor(((Number) this.yPos.getValue()).intValue()), ClickGui.CONTEXT.getColorScheme().doesTextShadow());
    }
}
