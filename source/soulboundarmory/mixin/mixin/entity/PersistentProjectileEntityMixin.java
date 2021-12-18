package soulboundarmory.mixin.mixin.entity;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PersistentProjectileEntity.class)
abstract class PersistentProjectileEntityMixin {
    // @Redirect(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    // private boolean
}
