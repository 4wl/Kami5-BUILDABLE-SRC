package tech.mmmax.kami.mixin.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.mmmax.kami.impl.features.modules.render.NoRender;

@Mixin({ EntityPlayer.class})
public class MixinEntityPlayer {

    @Inject(
        method = { "isEntityInsideOpaqueBlock"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void isEntityInsideOpaqueBlockHook(CallbackInfoReturnable info) {
        if (NoRender.INSTANCE.isEnabled()) {
            info.setReturnValue(Boolean.valueOf(false));
        }

    }
}
