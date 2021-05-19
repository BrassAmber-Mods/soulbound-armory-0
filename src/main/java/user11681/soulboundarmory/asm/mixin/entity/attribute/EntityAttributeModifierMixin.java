package user11681.soulboundarmory.asm.mixin.entity.attribute;

import java.util.UUID;
import net.minecraft.entity.attribute.AttributeModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import user11681.soulboundarmory.util.AttributeModifierIdentifiers;

@Mixin(AttributeModifier.class)
public abstract class AttributeModifierMixin {
    @Shadow
    @Final
    @Mutable
    private UUID uuid;

    @Inject(method = "<init>(Ljava/util/UUID;Ljava/lang/String;DLnet/minecraft/entity/attribute/AttributeModifier$Operation;)V", at = @At("RETURN"))
    public void construct(UUID uuid, final String name, final double value, final AttributeModifier.Operation operation, final CallbackInfo info) {
        final UUID original = AttributeModifierIdentifiers.get(uuid);

        if (original != null) {
            this.uuid = original;
        }
    }
}
