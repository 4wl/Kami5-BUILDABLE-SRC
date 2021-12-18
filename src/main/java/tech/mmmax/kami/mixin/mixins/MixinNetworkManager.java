package tech.mmmax.kami.mixin.mixins;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.mmmax.kami.api.event.PacketEvent;
import tech.mmmax.kami.api.event.PostPacketEvent;

@Mixin({ NetworkManager.class})
public class MixinNetworkManager {

    @Inject(
        method = { "sendPacket(Lnet/minecraft/network/Packet;)V"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    private void onSendPacket(Packet packet, CallbackInfo callbackInfo) {
        PacketEvent packetEvent = new PacketEvent(packet, PacketEvent.Time.Send);

        MinecraftForge.EVENT_BUS.post(packetEvent);
        if (packetEvent.isCanceled()) {
            callbackInfo.cancel();
        }

    }

    @Inject(
        method = { "sendPacket(Lnet/minecraft/network/Packet;)V"},
        at = {             @At("TAIL")},
        cancellable = true
    )
    private void postSendPacket(Packet packet, CallbackInfo callbackInfo) {
        PostPacketEvent packetEvent = new PostPacketEvent(packet);

        MinecraftForge.EVENT_BUS.post(packetEvent);
        if (packetEvent.isCanceled()) {
            callbackInfo.cancel();
        }

    }

    @Inject(
        method = { "channelRead0"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    private void onChannelRead(ChannelHandlerContext channelHandlerContext, Packet packet, CallbackInfo callbackInfo) {
        PacketEvent packetEvent = new PacketEvent(packet, PacketEvent.Time.Receive);

        MinecraftForge.EVENT_BUS.post(packetEvent);
        if (packetEvent.isCanceled()) {
            callbackInfo.cancel();
        }

    }

    @Inject(
        method = { "channelRead0"},
        at = {             @At("TAIL")},
        cancellable = true
    )
    private void postChannelRead(ChannelHandlerContext channelHandlerContext, Packet packet, CallbackInfo callbackInfo) {
        PostPacketEvent packetEvent = new PostPacketEvent(packet);

        MinecraftForge.EVENT_BUS.post(packetEvent);
        if (packetEvent.isCanceled()) {
            callbackInfo.cancel();
        }

    }

    @Inject(
        method = { "exceptionCaught"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void exception(ChannelHandlerContext context, Throwable throwable, CallbackInfo info) {
        info.cancel();
    }
}
