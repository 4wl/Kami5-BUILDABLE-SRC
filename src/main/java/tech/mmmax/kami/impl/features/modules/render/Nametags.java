package tech.mmmax.kami.impl.features.modules.render;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;

public class Nametags extends Module {

    public static Nametags INSTANCE;

    public Nametags() {
        super("Nametags", Feature.Category.Render);
        Nametags.INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {}
}
