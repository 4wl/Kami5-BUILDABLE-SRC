package tech.mmmax.kami.mixin.mixins.access;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ Minecraft.class})
public interface IMinecraft {

    @Accessor("timer")
    Timer getTimer();
}
