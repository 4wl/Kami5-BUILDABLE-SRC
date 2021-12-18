package tech.mmmax.kami.mixin.mixins.access;

import net.minecraft.network.play.server.SPacketExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ SPacketExplosion.class})
public interface ISPacketExplosion {

    @Accessor("motionX")
    void setMotionX(float f);

    @Accessor("motionY")
    void setMotionY(float f);

    @Accessor("motionZ")
    void setMotionZ(float f);
}
