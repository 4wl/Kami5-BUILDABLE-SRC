package tech.mmmax.kami.impl.features.modules.misc;

import java.util.function.Consumer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.utils.Timer;
import tech.mmmax.kami.api.utils.chat.ChatMessage;
import tech.mmmax.kami.api.utils.chat.ChatUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;

public class Disabler extends Module {

    Timer timer = new Timer();
    Value mode = (new ValueBuilder()).withDescriptor("Mode").withValue("NCP").withModes(new String[] { "NCP", "AAC"}).register(this);
    Value debug = (new ValueBuilder()).withDescriptor("Debug").withValue(Boolean.valueOf(false)).register(this);
    Value constant = (new ValueBuilder()).withDescriptor("Constant").withValue(Boolean.valueOf(false)).register(this);
    Value delay = (new ValueBuilder()).withDescriptor("Delay").withValue(Integer.valueOf(200)).withRange(Integer.valueOf(0), Integer.valueOf(2000)).withAction((set) -> {
        this.timer.setDelay(((Number) set.getValue()).longValue());
    }).register(this);

    public Disabler() {
        super("Disabler", Feature.Category.Misc);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!NullUtils.nullCheck() && event != null) {
            if (event.getPacket() instanceof SPacketPlayerAbilities) {
                SPacketPlayerAbilities packet = (SPacketPlayerAbilities) event.getPacket();
            }

            if (((String) this.mode.getValue()).equals("NCP") && event.getPacket() instanceof CPacketPlayerAbilities) {
                CPacketPlayerAbilities packet1 = (CPacketPlayerAbilities) event.getPacket();

                packet1.setAllowFlying(true);
                packet1.setInvulnerable(true);
                packet1.setWalkSpeed(3.0F);
                packet1.setCreativeMode(true);
                if (((Boolean) this.debug.getValue()).booleanValue()) {
                    ChatUtils.sendMessage(new ChatMessage("Editing player abilities", false, 0));
                }
            }

        }
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (!NullUtils.nullCheck()) {
            if (((String) this.mode.getValue()).equals("NCP") && ((Boolean) this.constant.getValue()).booleanValue() && this.timer.isPassed()) {
                CPacketPlayerAbilities packet = new CPacketPlayerAbilities(Disabler.mc.player.capabilities);

                Disabler.mc.getConnection().sendPacket(packet);
                this.timer.resetDelay();
            }

        }
    }

    public String getHudInfo() {
        return (String) this.mode.getValue();
    }
}
