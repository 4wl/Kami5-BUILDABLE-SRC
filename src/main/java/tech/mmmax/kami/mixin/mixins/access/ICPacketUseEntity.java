package tech.mmmax.kami.mixin.mixins.access;

import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ CPacketUseEntity.class})
public interface ICPacketUseEntity {

    @Accessor("entityId")
    void setEntityId(int i);

    @Accessor("action")
    void setAction(Action action);
}
