package soulboundarmory.mixin.mixin.entity.player;

import soulboundarmory.mixin.access.entity.PlayerEntityAccess;
import soulboundarmory.entity.SAAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccess {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(method = "attack", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z"))
    private float applyCriticalStrikeRate(float damage) {
        // AttributeInstance instance = self.getAttributeInstance(SoulboundArmoryAttributes.GENERIC_CRITICAL_STRIKE_PROBABILITY);
        //
        // if (instance != null) {
        //      AttributeModifier modifier = instance.getModifier(SoulboundArmoryAttributes.CRITICAL_STRIKE_PROBABILITY_MODIFIER_ID);
        //
        //     if (modifier != null) {
        //         return modifier.getValue() > self.getRandom().nextDouble() ? 2 * damage : damage;
        //     }
        // }
        //
        // return damage;

        var instance = this.getAttribute(SAAttributes.criticalStrikeRate);

        return instance != null && instance.getValue() > this.rand.nextDouble() ? 2 * damage : damage;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo info) {

    }
}
