package tech.mmmax.kami.mixin.mixins.access;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ NetHandlerPlayClient.class})
public interface INetHandlerPlayClient {

    @Accessor("netManager")
    void setNetManager(NetworkManager networkmanager);
}
