package tech.mmmax.kami.mixin.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tech.mmmax.kami.impl.features.modules.misc.MultiTask;

@Mixin({ Minecraft.class})
public class MixinMinecraft {

    @Redirect(
        method = { "sendClickBlockToController"},
        at =             @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"
            )
    )
    private boolean isHandActiveWrapper(EntityPlayerSP playerSP) {
        return !MultiTask.INSTANCE.isEnabled() && playerSP.isHandActive();
    }

    @Redirect(
        method = { "rightClickMouse"},
        at =             @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z",
                ordinal = 0
            ),
        require = 1
    )
    private boolean isHittingBlockHook(PlayerControllerMP playerControllerMP) {
        return !MultiTask.INSTANCE.isEnabled() && playerControllerMP.getIsHittingBlock();
    }
}
