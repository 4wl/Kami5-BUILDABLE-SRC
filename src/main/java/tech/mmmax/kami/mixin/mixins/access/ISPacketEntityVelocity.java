package tech.mmmax.kami.mixin.mixins.access;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ SPacketEntityVelocity.class})
public interface ISPacketEntityVelocity {

    @Accessor("motionX")
    void setMotionX(int i);

    @Accessor("motionY")
    void setMotionY(int i);

    @Accessor("motionZ")
    void setMotionZ(int i);
}
