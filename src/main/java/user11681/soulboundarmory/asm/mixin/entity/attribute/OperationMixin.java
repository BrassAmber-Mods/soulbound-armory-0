package user11681.soulboundarmory.asm.mixin.entity.attribute;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AttributeModifier.Operation.class)
abstract class OperationMixin {
    @Shadow
    @Final
    private int id;

    @Inject(method = "fromId", at = @At("HEAD"), cancellable = true)
    private static void getIdModSupport(int id, CallbackInfoReturnable<AttributeModifier.Operation> info) {
        for (AttributeModifier.Operation operation : AttributeModifier.Operation.values()) {
            //noinspection ConstantConditions
            if (((OperationMixin) (Object) operation).id == id) {
                info.setReturnValue(operation);
            }
        }
    }
}
