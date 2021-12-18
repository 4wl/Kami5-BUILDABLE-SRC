package tech.mmmax.kami.api.feature.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.api.wrapper.IMinecraft;

public class HudComponent extends Feature implements IMinecraft {

    public Value xPos = (new ValueBuilder()).withDescriptor("X Pos").withValue(Integer.valueOf(100)).withRange(Integer.valueOf(0), Integer.valueOf(1000)).register(this);
    public Value yPos = (new ValueBuilder()).withDescriptor("Y Pos").withValue(Integer.valueOf(10)).withRange(Integer.valueOf(0), Integer.valueOf(1000)).register(this);
    ScaledResolution sr;

    public HudComponent(String name) {
        super(name, Feature.Category.Hud, Feature.FeatureType.Hud);
    }

    @SubscribeEvent
    public void draw(Text event) {
        this.sr = new ScaledResolution(HudComponent.mc);
        this.xPos.setMax(Integer.valueOf(this.sr.getScaledWidth()));
        this.yPos.setMax(Integer.valueOf(this.sr.getScaledHeight()));
    }
}
