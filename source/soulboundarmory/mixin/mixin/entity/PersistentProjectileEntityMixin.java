package soulboundarmory.mixin.mixin.entity;

import soulboundarmory.mixin.access.PersistentProjectileEntityAccess;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PersistentProjectileEntity.class)
abstract class PersistentProjectileEntityMixin implements PersistentProjectileEntityAccess {
    @Accessor("life")
    @Override
    public abstract int life();
}
