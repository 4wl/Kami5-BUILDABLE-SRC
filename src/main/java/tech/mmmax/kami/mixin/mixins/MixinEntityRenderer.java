package tech.mmmax.kami.mixin.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tech.mmmax.kami.api.event.PerspectiveEvent;

@Mixin({ EntityRenderer.class})
public class MixinEntityRenderer {

    @Shadow
    @Final
    public Minecraft mc;

    @Redirect(
        method = { "setupCameraTransform"},
        at =             @At(
                value = "INVOKE",
                target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"
            )
    )
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);

        MinecraftForge.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(
        method = { "renderWorldPass"},
        at =             @At(
                value = "INVOKE",
                target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"
            )
    )
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);

        MinecraftForge.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }

    @Redirect(
        method = { "renderCloudsCheck"},
        at =             @At(
                value = "INVOKE",
                target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"
            )
    )
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) this.mc.displayWidth / (float) this.mc.displayHeight);

        MinecraftForge.EVENT_BUS.post(event);
        Project.gluPerspective(fovy, event.getAspect(), zNear, zFar);
    }
}
