package tech.mmmax.kami.mixin.mixins;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tech.mmmax.kami.api.event.RenderItemEvent;

@Mixin({ ItemRenderer.class})
public class MixinItemRenderer {

    @Redirect(
        method = { "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"},
        at =             @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/renderer/ItemRenderer;transformSideFirstPerson(Lnet/minecraft/util/EnumHandSide;F)V"
            )
    )
    public void transformRedirect(ItemRenderer renderer, EnumHandSide hand, float y) {
        RenderItemEvent event = new RenderItemEvent(0.5600000023841858D, (double) (-0.52F + y * -0.6F), -0.7200000286102295D, -0.5600000023841858D, (double) (-0.52F + y * -0.6F), -0.7200000286102295D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D);

        MinecraftForge.EVENT_BUS.post(event);
        if (hand == EnumHandSide.RIGHT) {
            GlStateManager.translate(event.getMainX(), event.getMainY(), event.getMainZ());
            GlStateManager.scale(event.getMainHandScaleX(), event.getMainHandScaleY(), event.getMainHandScaleZ());
            GlStateManager.rotate((float) event.getMainRAngel(), (float) event.getMainRx(), (float) event.getMainRy(), (float) event.getMainRz());
        } else {
            GlStateManager.translate(event.getOffX(), event.getOffY(), event.getOffZ());
            GlStateManager.scale(event.getOffHandScaleX(), event.getOffHandScaleY(), event.getOffHandScaleZ());
            GlStateManager.rotate((float) event.getOffRAngel(), (float) event.getOffRx(), (float) event.getOffRy(), (float) event.getOffRz());
        }

    }
}
