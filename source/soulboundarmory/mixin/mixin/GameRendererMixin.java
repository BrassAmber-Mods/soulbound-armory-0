package soulboundarmory.mixin.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9))
    public double extendEntityReach(double nine) {
        var distance = this.client.interactionManager.getReachDistance();
        return distance * distance;
    }
}
