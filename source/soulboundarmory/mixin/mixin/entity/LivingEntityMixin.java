package soulboundarmory.mixin.mixin.entity;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.skill.Skills;
import soulboundarmory.util.EntityUtil;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
	@Shadow protected abstract void dropXp();

	@Shadow protected abstract int getXpToDrop();

	@Inject(method = "tickCramming",
	        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"),
	        locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void freeze(CallbackInfo info, List<Entity> entities) {
		if ((Object) this instanceof ServerPlayerEntity player) {
			var greatsword = ItemComponentType.greatsword.of(player);
			var leapForce = greatsword.leapForce();

			if (leapForce > 0) {
				if (greatsword.hasSkill(Skills.freezing)) {
					entities.stream().filter(greatsword::canFreeze).forEach(entity -> greatsword.freeze(entity, (int) (20 * leapForce), (float) EntityUtil.speed(player) * leapForce));
				}

				if (greatsword.leapDuration <= 0 && player.isOnGround() && (player.getVelocity().y <= 0.01 || player.isCreative())) {
					greatsword.leapDuration = 7;
				}

				if (player.isInLava()) {
					greatsword.resetLeapForce();
				}
			}
		}
	}

	@Redirect(method = "drop", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;dropXp()V"))
	private void givePlayerXPFromEnderPull(LivingEntity entity, DamageSource source) {
		var mixin = (LivingEntityMixin) (Object) entity;
		ItemComponent.fromAttacker(entity, source)
			.filter(component -> component.hasSkill(Skills.enderPull))
			.ifPresentOrElse(component -> component.player.addExperience(mixin.getXpToDrop()), mixin::dropXp);
	}
}
