package tech.mmmax.kami.mixin.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemFood;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tech.mmmax.kami.api.event.ChorusEvent;

@Mixin({ ItemChorusFruit.class})
public class MixinItemChorusFruit extends ItemFood {

    public MixinItemChorusFruit(int amount, float saturation) {
        super(amount, saturation, false);
    }

    @Redirect(
        method = { "onItemUseFinish"},
        at =             @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/EntityLivingBase;attemptTeleport(DDD)Z"
            )
    )
    public boolean attemptTeleportHook(EntityLivingBase entityLivingBase, double x, double y, double z) {
        ChorusEvent event = new ChorusEvent(entityLivingBase, x, y, z);

        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled() ? entityLivingBase.attemptTeleport(x, y, z) : event.isSuccessful();
    }
}
