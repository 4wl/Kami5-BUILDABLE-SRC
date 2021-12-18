package tech.mmmax.kami.impl.features.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.ToIntFunction;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.hud.HudComponent;
import tech.mmmax.kami.api.management.FeatureManager;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.features.modules.client.HudColors;
import tech.mmmax.kami.impl.gui.ClickGui;

public class FeatureList extends HudComponent {

    Value alignment = (new ValueBuilder()).withDescriptor("Alignment").withValue("TopLeft").withModes(new String[] { "TopLeft", "BottomLeft", "TopRight", "BottomRight"}).register(this);

    public FeatureList() {
        super("Feature List");
    }

    public void draw(Text event) {
        super.draw(event);
        ArrayList sorted = new ArrayList();
        Iterator offset = FeatureManager.INSTANCE.getFeatures().iterator();

        String string;

        while (offset.hasNext()) {
            Feature feature = (Feature) offset.next();

            if (((Boolean) feature.visible.getValue()).booleanValue() && feature.isEnabled()) {
                string = feature.getDisplayName() + (!feature.getHudInfo().equals("") ? ChatFormatting.GRAY + " [" + ChatFormatting.WHITE + feature.getHudInfo() + ChatFormatting.GRAY + "]" : "");
                sorted.add(string);
            }
        }

        sorted.sort(Comparator.comparingInt((str) -> {
            int o = ClickGui.CONTEXT.getRenderer().getTextWidth(str);

            return -o;
        }));
        int offset1 = 0;
        Iterator feature1 = sorted.iterator();

        while (feature1.hasNext()) {
            string = (String) feature1.next();
            boolean top = ((String) this.alignment.getValue()).contains("Top");

            if (((String) this.alignment.getValue()).contains("Left")) {
                ClickGui.CONTEXT.getRenderer().renderText(string, (float) ((Number) this.xPos.getValue()).intValue(), (float) (((Number) this.yPos.getValue()).intValue() + (top ? offset1 : -offset1)), HudColors.getTextColor(((Number) this.yPos.getValue()).intValue() + (top ? offset1 : -offset1)), ClickGui.CONTEXT.getColorScheme().doesTextShadow());
                offset1 += ClickGui.CONTEXT.getRenderer().getTextHeight(string);
            }

            if (((String) this.alignment.getValue()).contains("Right")) {
                ClickGui.CONTEXT.getRenderer().renderText(string, (float) (((Number) this.xPos.getValue()).intValue() - ClickGui.CONTEXT.getRenderer().getTextWidth(string)), (float) (((Number) this.yPos.getValue()).intValue() + (top ? offset1 : -offset1)), HudColors.getTextColor(((Number) this.yPos.getValue()).intValue() + (top ? offset1 : -offset1)), ClickGui.CONTEXT.getColorScheme().doesTextShadow());
                offset1 += ClickGui.CONTEXT.getRenderer().getTextHeight(string);
            }
        }

    }
}
