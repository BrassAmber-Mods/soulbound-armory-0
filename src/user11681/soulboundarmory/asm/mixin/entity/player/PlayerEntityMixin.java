package user11681.soulboundarmory.asm.mixin.entity.player;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import user11681.soulboundarmory.asm.access.entity.PlayerEntityAccess;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccess {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(method = "attack", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;hurt(Lnet/minecraft/util/DamageSource;F)Z"))
    private float applyCriticalStrikeRate(float damage) {
        // EntityAttributeInstance instance = self.getEntityAttributeInstance(SoulboundArmoryAttributes.GENERIC_CRITICAL_STRIKE_PROBABILITY);
        //
        // if (instance != null) {
        //      EntityAttributeModifier modifier = instance.getModifier(SoulboundArmoryAttributes.CRITICAL_STRIKE_PROBABILITY_MODIFIER_ID);
        //
        //     if (modifier != null) {
        //         return modifier.getValue() > self.getRandom().nextDouble() ? 2 * damage : damage;
        //     }
        // }
        //
        // return damage;

        EntityAttributeInstance instance = this.getAttributeInstance(user11681.soulboundarmory.entity.SAAttributes.criticalStrikeRate);

        return instance != null && instance.getValue() > this.random.nextDouble() ? 2 * damage : damage;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo info) {

    }
}
