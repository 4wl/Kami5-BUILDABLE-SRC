package tech.mmmax.kami.mixin.mixins.access;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ Entity.class})
public interface IEntity {

    @Accessor("inPortal")
    boolean isInPortal();
}
