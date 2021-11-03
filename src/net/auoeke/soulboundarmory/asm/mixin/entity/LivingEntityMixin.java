package net.auoeke.soulboundarmory.asm.mixin.entity;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.auoeke.soulboundarmory.capability.Capabilities;
import net.auoeke.soulboundarmory.capability.soulbound.item.StorageType;
import net.auoeke.soulboundarmory.capability.soulbound.item.weapon.GreatswordStorage;
import net.auoeke.soulboundarmory.registry.Skills;
import net.auoeke.soulboundarmory.util.EntityUtil;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> p_i48580_1_, World p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"), cancellable = true)
    private static void createSoulboundArmoryAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        // info.getReturnValue().add(SAAttributes.criticalStrikeRate, 0).add(SAAttributes.efficiency, 1);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "pushEntities",
            at = @At(value = "INVOKE_ASSIGN",
                     target = "Lnet/minecraft/world/World;getEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    protected void freeze(CallbackInfo info, List<Entity> entities) {
        if ((Object) this instanceof PlayerEntity player && !this.world.isClient) {
            GreatswordStorage greatsword = Capabilities.weapon.get(player).storage(StorageType.greatsword);
            double leapForce = greatsword.leapForce();

            if (leapForce > 0) {
                if (greatsword.hasSkill(Skills.freezing)) {
                    for (Entity nearbyEntity : entities) {
                        greatsword.freeze(nearbyEntity, (int) (20 * leapForce), (float) EntityUtil.speed(player) * (float) leapForce);
                    }
                }

                if (greatsword.leapDuration() <= 0 && player.isOnGround() && (player.getVelocity().y <= 0.01 || player.isCreative())) {
                    greatsword.leapDuration(7);
                }

                if (player.isInLava()) {
                    greatsword.resetLeapForce();
                }
            }
        }
    }
}
