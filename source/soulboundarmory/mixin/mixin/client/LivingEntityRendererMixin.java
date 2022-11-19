package soulboundarmory.mixin.mixin.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.component.Components;
import soulboundarmory.util.Math2;

@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin {
	@ModifyConstant(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	                slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getOverlay(Lnet/minecraft/entity/LivingEntity;F)I")),
	                constant = @Constant(floatValue = 1, ordinal = 0))
	private float renderEntityBlueWhileFrozenR(float red, LivingEntity entity) {
		var overlay = Components.entityData.of(entity).overlay();
		return overlay == 0 ? red : Math2.red(overlay) / 255F;
	}

	@ModifyConstant(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	                slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getOverlay(Lnet/minecraft/entity/LivingEntity;F)I")),
	                constant = @Constant(floatValue = 1, ordinal = 1))
	private float renderEntityBlueWhileFrozenG(float green, LivingEntity entity) {
		var overlay = Components.entityData.of(entity).overlay();
		return overlay == 0 ? green : Math2.green(overlay) / 255F;
	}

	@ModifyConstant(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	                slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getOverlay(Lnet/minecraft/entity/LivingEntity;F)I")),
	                constant = @Constant(floatValue = 1, ordinal = 2))
	private float renderEntityBlueWhileFrozenB(float blue, LivingEntity entity) {
		var overlay = Components.entityData.of(entity).overlay();
		return overlay == 0 ? blue : Math2.blue(overlay) / 255F;
	}

	@Inject(method = "getOverlay", at = @At("RETURN"), cancellable = true)
	private static void removeBlueColorFromFrozenEntity(LivingEntity entity, float whiteOverlayProgress, CallbackInfoReturnable<Integer> info) {
		if (Components.entityData.of(entity).overlay() != 0) {
			info.setReturnValue(OverlayTexture.packUv(info.getReturnValueI() & 0xFFFF, OverlayTexture.getV(false)));
		}
	}

	@ModifyVariable(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
	                at = @At(value = "STORE"),
	                index = 12)
	private float cancelAnimationProgressForFrozenEntity(float progress, LivingEntity entity) {
		var component = Components.entityData.of(entity);
		return component.isFrozen() ? component.animationProgress == -1 ? component.animationProgress = progress : component.animationProgress : progress;
	}
}
