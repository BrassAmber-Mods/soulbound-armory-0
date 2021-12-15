package soulboundarmory.mixin.mixin.entity;

import java.util.UUID;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import soulboundarmory.util.AttributeModifierIdentifiers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityAttributeModifier.class)
abstract class EntityAttributeModifierMixin {
    @Shadow
    @Final
    @Mutable
    private UUID uuid;

    @Inject(method = "<init>(Ljava/util/UUID;Ljava/lang/String;DLnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;)V", at = @At("RETURN"))
    public void construct(UUID uuid, String name, double value, EntityAttributeModifier.Operation operation, CallbackInfo info) {
        var original = AttributeModifierIdentifiers.get(uuid);

        if (original != null) {
            this.uuid = original;
        }
    }
}
