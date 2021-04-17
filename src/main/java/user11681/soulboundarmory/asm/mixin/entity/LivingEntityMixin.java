package user11681.soulboundarmory.asm.mixin.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import user11681.soulboundarmory.entity.SoulboundArmoryAttributes;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "createLivingAttributes", at = @At("RETURN"), cancellable = true)
    private static void createSoulboundArmoryAttributes(final CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        info.getReturnValue()
                .add(SoulboundArmoryAttributes.GENERIC_CRITICAL_STRIKE_PROBABILITY, 0)
                .add(SoulboundArmoryAttributes.GENERIC_EFFICIENCY, 1);
    }
}
