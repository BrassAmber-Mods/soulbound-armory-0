package soulboundarmory.mixin.mixin.entity;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import soulboundarmory.entity.SoulboundDaggerEntity;

@Mixin(PersistentProjectileEntity.class)
abstract class PersistentProjectileEntityMixin {
	@ModifyVariable(method = "tick", ordinal = 0, at = @At(value = "LOAD", ordinal = 2))
	private boolean enableEntityCollisionForNoClipSoulboundDagger(boolean noClip) {
		return noClip && !((Object) this instanceof SoulboundDaggerEntity);
	}
}
