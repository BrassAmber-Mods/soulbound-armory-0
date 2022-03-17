package soulboundarmory.mixin.mixin.client;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import soulboundarmory.item.SoulboundItems;

@Mixin(HeldItemRenderer.class)
abstract class HeldItemRendererMixin {
    @ModifyConstant(method = "renderFirstPersonItem", constant = @Constant(doubleValue = 0.7F))
    private double lowerDaggerTranslation(double pointSeven, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        return item.isOf(SoulboundItems.dagger) ? 0.4 : pointSeven;
    }

    @ModifyConstant(method = "renderFirstPersonItem", constant = @Constant(doubleValue = 0.1F, ordinal = 0))
    private double translateDaggerRight(double pointOne, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        return item.isOf(SoulboundItems.dagger) ? 0.4 : pointOne;
    }
}
