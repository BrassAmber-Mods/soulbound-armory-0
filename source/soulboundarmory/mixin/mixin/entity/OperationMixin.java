package soulboundarmory.mixin.mixin.entity;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAttributeModifier.Operation.class)
abstract class OperationMixin {
	@Shadow
	@Final
	private int id;

	@Inject(method = "fromId", at = @At("HEAD"), cancellable = true)
	private static void getIdModSupport(int id, CallbackInfoReturnable<EntityAttributeModifier.Operation> info) {
		for (var operation : EntityAttributeModifier.Operation.values()) {
			if (((OperationMixin) (Object) operation).id == id) {
				info.setReturnValue(operation);
			}
		}
	}
}
