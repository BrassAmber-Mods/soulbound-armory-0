package net.auoeke.soulboundarmory.asm.mixin.entity.projectile;

import net.auoeke.soulboundarmory.asm.access.entity.AbstractArrowEntityAccess;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractArrowEntity.class)
abstract class AbstractArrowEntityMixin implements AbstractArrowEntityAccess {
    @Accessor
    @Override
    public abstract int life();
}
