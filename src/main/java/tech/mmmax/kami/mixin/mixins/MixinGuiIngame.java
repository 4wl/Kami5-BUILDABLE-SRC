package tech.mmmax.kami.mixin.mixins;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.mmmax.kami.api.event.RenderCrosshairEvent;

@Mixin({ GuiIngame.class})
public class MixinGuiIngame {

    @Inject(
        method = { "renderAttackIndicator"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void renderAttackIndicatorHook(float partialTicks, ScaledResolution sr, CallbackInfo ci) {
        RenderCrosshairEvent event = new RenderCrosshairEvent(partialTicks, sr);

        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }

    }
}
