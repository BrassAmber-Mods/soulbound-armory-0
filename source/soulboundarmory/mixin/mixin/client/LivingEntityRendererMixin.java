package soulboundarmory.mixin.mixin.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import soulboundarmory.component.Components;

@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin {
    @ModifyArgs(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    private void renderEntityBlueWhileFrozen(Args args, LivingEntity entity, float yaw, float tickDelta, MatrixStack matrixes, VertexConsumerProvider vertexConsumers, int light) {
        if (Components.entityData.of(entity).isFrozen()) {
            args.set(4, 0x3E / 255F);
            args.set(5, 0xDB / 255F);
            args.set(6, 0xFF / 255F);
        }
    }

    @Inject(method = "getOverlay", at = @At("RETURN"), cancellable = true)
    private static void removeBlueColorFromFrozenEntity(LivingEntity entity, float whiteOverlayProgress, CallbackInfoReturnable<Integer> info) {
        if (Components.entityData.of(entity).isFrozen()) {
            info.setReturnValue(OverlayTexture.packUv(info.getReturnValueI() & 0xFFFF, OverlayTexture.getV(false)));
        }
    }

    @ModifyVariable(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                    at = @At(value = "HEAD"),
                    ordinal = 1)
    private float stopAnimationProgress(float tickDelta, LivingEntity entity) {
        var component = Components.entityData.of(entity);
        return component.isFrozen() ? component.tickDelta : tickDelta;
    }
}
