package tech.mmmax.kami.mixin.mixins.access;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ RenderEnderCrystal.class})
public interface IRenderEnderCrystal {

    @Accessor("modelEnderCrystalNoBase")
    ModelBase getModelEnderCrystalNoBase();

    @Accessor("modelEnderCrystalNoBase")
    void setModelEnderCrystalNoBase(ModelBase modelbase);
}
