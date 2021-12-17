package soulboundarmory.mixin.mixin.client;

import cell.client.gui.screen.CellScreen;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import soulboundarmory.client.gui.bar.ExperienceBar;
import soulboundarmory.client.gui.screen.SoulboundScreen;
import soulboundarmory.config.Configuration;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void renderSoulboundItemExperienceBar(MatrixStack matrixes, int x, CallbackInfo info) {
        if (CellScreen.cellScreen() instanceof SoulboundScreen || Configuration.instance().client.overlayExperienceBar && ExperienceBar.overlayBar.renderOverlay(matrixes)) {
            info.cancel();
        }
    }
}
