package tech.mmmax.kami.mixin.mixins.access;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ SPacketPlayerPosLook.class})
public interface ISPacketPlayerPosLook {

    @Accessor("yaw")
    void setYaw(float f);

    @Accessor("pitch")
    void setPitch(float f);

    @Accessor("x")
    void setX(double d0);

    @Accessor("y")
    void setY(double d0);

    @Accessor("z")
    void setZ(double d0);
}
