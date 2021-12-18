package tech.mmmax.kami.mixin.mixins.access;

import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ Timer.class})
public interface ITimer {

    @Accessor("tickLength")
    float getTickLength();

    @Accessor("tickLength")
    void setTickLength(float f);
}
