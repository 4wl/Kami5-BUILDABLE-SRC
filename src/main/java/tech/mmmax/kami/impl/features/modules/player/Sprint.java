package tech.mmmax.kami.impl.features.modules.player;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", Feature.Category.Player);
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent tickEvent) {
        if (!NullUtils.nullCheck()) {
            if (Sprint.mc.player != null && Sprint.mc.world != null) {
                if (Sprint.mc.player.moveForward > 0.0F) {
                    Sprint.mc.player.setSprinting(true);
                }

            }
        }
    }
}
