package user11681.usersmanual.asm.mixin.entity.attribute;

import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Operation.class)
public abstract class OperationMixin {
    @Shadow
    @Final
    private int id;

    @Inject(method = "fromId", at = @At("HEAD"), cancellable = true)
    private static void getIdModSupport(final int id, final CallbackInfoReturnable<Operation> info) {
        for (final Operation operation : Operation.values()) {
            //noinspection ConstantConditions
            if (((OperationMixin) (Object) operation).id == id) {
                info.setReturnValue(operation);
            }
        }
    }
}
