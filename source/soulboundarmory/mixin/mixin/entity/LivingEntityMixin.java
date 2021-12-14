package soulboundarmory.mixin.mixin.entity;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.registry.Skills;
import soulboundarmory.util.EntityUtil;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
    @Inject(method = "tickCramming",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void freeze(CallbackInfo info, List<Entity> entities) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            var greatsword = ItemComponentType.greatsword.of(player);
            var leapForce = greatsword.leapForce();

            if (leapForce > 0) {
                if (greatsword.hasSkill(Skills.freezing)) {
                    for (var nearbyEntity : entities) {
                        greatsword.freeze(nearbyEntity, (int) (20 * leapForce), (float) EntityUtil.speed(player) * (float) leapForce);
                    }
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
}
