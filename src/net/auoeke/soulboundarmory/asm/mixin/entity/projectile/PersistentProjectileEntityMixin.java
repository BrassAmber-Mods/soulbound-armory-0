package net.auoeke.soulboundarmory.asm.mixin.entity.projectile;

import net.auoeke.soulboundarmory.asm.access.entity.PersistentProjectileEntityAccess;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PersistentProjectileEntity.class)
abstract class PersistentProjectileEntityMixin implements PersistentProjectileEntityAccess {
    @Accessor
    @Override
    public abstract int getLife();
}
