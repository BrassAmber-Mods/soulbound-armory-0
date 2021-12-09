package soulboundarmory.mixin.mixin.entity.projectile;

import soulboundarmory.mixin.access.AbstractArrowEntityAccess;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PersistentProjectileEntity.class)
abstract class AbstractArrowEntityMixin implements AbstractArrowEntityAccess {
    @Accessor("life")
    @Override
    public abstract int life();
}
