package tech.mmmax.kami.impl.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.world.HoleUtils;

public class Safety extends Module {

    Safety.SafetyMode safety;

    public Safety() {
        super("Safety", Feature.Category.Client);
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (Safety.mc.world.getBlockState(Safety.mc.player.getPosition()).getMaterial().isSolid()) {
                this.safety = Safety.SafetyMode.SAFE;
            } else if (HoleUtils.isHole(Safety.mc.player.getPosition())) {
                this.safety = Safety.SafetyMode.SAFE;
            } else {
                this.safety = Safety.SafetyMode.UNSAFE;
            }
        }
    }

    public String getHudInfo() {
        return this.safety != null ? this.safety.toString() : "";
    }

    static enum SafetyMode {

        SAFE(ChatFormatting.GREEN), UNSAFE(ChatFormatting.RED);

        ChatFormatting color;

        private SafetyMode(ChatFormatting color) {
            this.color = color;
        }

        public String toString() {
            return this.color.toString() + super.toString();
        }
    }
}
