package tech.mmmax.kami.impl.features.modules.misc;

import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class MultiTask extends Module {

    public static MultiTask INSTANCE;
    public Value cancel = (new ValueBuilder()).withDescriptor("Cancel").withValue(Boolean.valueOf(false)).register(this);

    public MultiTask() {
        super("MultiTask", Feature.Category.Misc);
        MultiTask.INSTANCE = this;
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof CPacketPlayerDigging && ((Boolean) this.cancel.getValue()).booleanValue()) {
            CPacketPlayerDigging packet = (CPacketPlayerDigging) event.getPacket();

            if (packet.getAction() == Action.RELEASE_USE_ITEM && MultiTask.mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.GOLDEN_APPLE) {
                event.setCanceled(true);
            }
        }

    }
}
