package tech.mmmax.kami.impl.features.modules.misc;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.feature.module.Module;
import tech.mmmax.kami.api.utils.NullUtils;
import tech.mmmax.kami.api.value.Value;
import tech.mmmax.kami.api.value.builder.ValueBuilder;
import tech.mmmax.kami.mixin.mixins.access.INetHandlerPlayClient;

public class NettyTest extends Module {

    Value cancelCPacket = (new ValueBuilder()).withDescriptor("Cancel C").withValue(Boolean.valueOf(false)).register(this);
    Value cancelKick = (new ValueBuilder()).withDescriptor("Cancel Kick").withValue(Boolean.valueOf(false)).register(this);
    Value switchNetty = (new ValueBuilder()).withDescriptor("Switch Netty").withValue(Boolean.valueOf(false)).register(this);
    NetworkManager networkManager = null;
    boolean cancel = false;

    public NettyTest() {
        super("NettyTest", Feature.Category.Misc);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof CPacketChatMessage && ((Boolean) this.switchNetty.getValue()).booleanValue() && this.networkManager != null) {
            ;
        }

        if (event.getTime() == PacketEvent.Time.Send && ((Boolean) this.cancelCPacket.getValue()).booleanValue()) {
            event.setCanceled(true);
        }

        if ((event.getPacket() instanceof SPacketDisconnect || event.getPacket() instanceof net.minecraft.network.login.server.SPacketDisconnect) && ((Boolean) this.cancelKick.getValue()).booleanValue()) {
            event.setCanceled(true);
        }

    }

    public void onDisable() {
        super.onDisable();
        if (!NullUtils.nullCheck()) {
            if (((Boolean) this.switchNetty.getValue()).booleanValue() && this.networkManager != null) {
                ((INetHandlerPlayClient) NettyTest.mc.getConnection()).setNetManager(this.networkManager);
            }

        }
    }

    public void onEnable() {
        super.onEnable();
        if (!NullUtils.nullCheck()) {
            if (((Boolean) this.switchNetty.getValue()).booleanValue()) {
                this.networkManager = NettyTest.mc.getConnection().getNetworkManager();
            }

        }
    }
}
