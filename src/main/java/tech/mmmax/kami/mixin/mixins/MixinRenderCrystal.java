package tech.mmmax.kami.mixin.mixins;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tech.mmmax.kami.api.event.RenderEntityEvent;

@Mixin(
    value = { RenderEnderCrystal.class},
    priority = 2147483646
)
public class MixinRenderCrystal {

    @Redirect(
        method = { "doRender"},
        at =             @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"
            )
    )
    public void renderModelBaseHook(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        RenderEntityEvent event = new RenderEntityEvent(modelBase, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }

    }
}
