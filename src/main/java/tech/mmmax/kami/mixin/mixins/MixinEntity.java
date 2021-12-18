package tech.mmmax.kami.mixin.mixins;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.mmmax.kami.api.event.PushOutOfBlocksEvent;

@Mixin({ Entity.class})
public class MixinEntity {

    @Inject(
        method = { "pushOutOfBlocks"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable cir) {
        PushOutOfBlocksEvent event = new PushOutOfBlocksEvent();

        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.cancel();
        }

    }
}
