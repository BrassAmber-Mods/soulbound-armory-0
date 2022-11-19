package soulboundarmory.mixin.mixin.entity;

import java.util.Optional;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.weapon.DaggerComponent;
import soulboundarmory.entity.SoulboundDaggerEntity;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin {
	@Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMainHandStack()Lnet/minecraft/item/ItemStack;"))
	private ItemStack attackWithThrownDagger(PlayerEntity player) {
		return this.daggerComponent().map(ItemComponent::stack).orElse(player.getMainHandStack());
	}

	@ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 0), name = "f")
	private float useThrownDaggerDamage(float damage) {
		return this.daggerComponent().map(component -> (float) component.attackDamage()).orElse(damage);
	}

	@ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 0), name = "f2")
	private float fixAttackCooldownOnThrownDagger(float cooldown) {
		return this.daggerComponent().map(component -> (float) SoulboundDaggerEntity.attacker.damageRatio).orElse(cooldown);
	}

	@Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;player(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/entity/damage/DamageSource;"))
	private DamageSource makeThrownDaggerSource(PlayerEntity player) {
		return this.dagger().map(dagger -> DamageSource.arrow(dagger, player)).orElse(DamageSource.player(player));
	}

	@Unique
	private Optional<DaggerComponent> daggerComponent() {
		return this.dagger().flatMap(SoulboundDaggerEntity::component);
	}

	@Unique
	private Optional<SoulboundDaggerEntity> dagger() {
		return Optional.ofNullable(SoulboundDaggerEntity.attacker);
	}
}
