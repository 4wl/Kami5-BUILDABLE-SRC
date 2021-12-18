package tech.mmmax.kami.impl.features.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.function.Consumer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.feature.hud.HudComponent;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.KamiMod;
import tech.mmmax.kami.impl.features.modules.client.HudColors;
import tech.mmmax.kami.impl.gui.ClickGui;

public class Welcomer extends HudComponent {

    Value autoPos = (new ValueBuilder()).withDescriptor("Auto Pos").withValue(Boolean.valueOf(true)).withAction((s) -> {
        this.xPos.setActive(!((Boolean) s.getValue()).booleanValue());
        this.yPos.setActive(!((Boolean) s.getValue()).booleanValue());
    }).register(this);

    public Welcomer() {
        super("Welcomer");
    }

    @SubscribeEvent
    public void onRender(Text event) {
        if (!NullUtils.nullCheck()) {
            String string = this.getWelcomeString();

            if (((Boolean) this.autoPos.getValue()).booleanValue()) {
                ScaledResolution sr = new ScaledResolution(Welcomer.mc);

                this.xPos.setValue(Integer.valueOf((sr.getScaledWidth() - ClickGui.CONTEXT.getRenderer().getTextWidth(string)) / 2));
                this.yPos.setValue(Integer.valueOf(1));
            }

            ClickGui.CONTEXT.getRenderer().renderText(this.getWelcomeString(), ((Number) this.xPos.getValue()).floatValue(), ((Number) this.yPos.getValue()).floatValue(), HudColors.getTextColor(((Number) this.yPos.getValue()).intValue()), ClickGui.CONTEXT.getColorScheme().doesTextShadow());
        }
    }

    String getWelcomeString() {
        return "Welcome to " + KamiMod.NAME_VERSION_COLORED + " " + ChatFormatting.RESET + Welcomer.mc.player.getName();
    }
}
