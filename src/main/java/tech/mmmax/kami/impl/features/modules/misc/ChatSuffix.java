package tech.mmmax.kami.impl.features.modules.misc;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.chat.ChatUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.impl.KamiMod;
import tech.mmmax.kami.mixin.mixins.access.ICPacketChat;

public class ChatSuffix extends Module {

    Value mode;
    String[] filters;

    public ChatSuffix() {
        super("Chat Suffix", Feature.Category.Misc);
        this.mode = (new ValueBuilder()).withDescriptor("Mode").withValue(KamiMod.NAME).register(this);
        this.filters = new String[] { ".", "/", ",", ":", "`", "-"};
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!NullUtils.nullCheck()) {
            if (event.getTime() == PacketEvent.Time.Send && event.getPacket() instanceof CPacketChatMessage && this.allowMessage(((CPacketChatMessage) event.getPacket()).getMessage())) {
                ((ICPacketChat) ((CPacketChatMessage) event.getPacket())).setMessage(((CPacketChatMessage) event.getPacket()).getMessage() + ChatUtils.hephaestus(" | " + ((String) this.mode.getValue()).toLowerCase()));
            }

        }
    }

    boolean allowMessage(String message) {
        boolean allow = true;
        String[] astring = this.filters;
        int i = astring.length;

        for (int j = 0; j < i; ++j) {
            String s = astring[j];

            if (message.startsWith(s)) {
                allow = false;
                break;
            }
        }

        return allow;
    }
}
