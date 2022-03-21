package soulboundarmory.mixin.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import soulboundarmory.client.gui.bar.ExperienceBar;
import soulboundarmory.client.gui.screen.SoulboundScreen;
import soulboundarmory.component.Components;
import soulboundarmory.config.Configuration;
import soulboundarmory.lib.gui.screen.ScreenWidget;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void renderSoulboundItemExperienceBar(MatrixStack matrixes, int x, CallbackInfo info) {
        if (ScreenWidget.cellScreen() instanceof SoulboundScreen screen && screen.xpBar.isVisible() || Configuration.instance().client.overlayExperienceBar && ExperienceBar.overlayBar.renderOverlay(matrixes)) {
            info.cancel();
        }
    }

    @Inject(method = "renderHotbarItem", at = @At("HEAD"), cancellable = true)
    private void hideSoulboundItemWhileTransforming(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed, CallbackInfo info) {
        var component = Components.entityData.of(player);

        // todo: fix condition
        if (component.animationProgress > 0 && component.unlockedStack.filter(item -> item.stack == stack).isPresent()) {
            info.cancel();
        }
    }
}
