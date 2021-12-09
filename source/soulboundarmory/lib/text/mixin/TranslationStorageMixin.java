package soulboundarmory.lib.text.mixin;

import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.lib.text.RomanNumerals;

@OnlyIn(Dist.CLIENT)
@Mixin(TranslationStorage.class)
abstract class TranslationStorageMixin {
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void get(String key, CallbackInfoReturnable<String> info) {
        if (key.matches("enchantment\\.level\\.\\d+")) {
            info.setReturnValue(RomanNumerals.fromDecimal(Integer.parseInt(key.replaceAll("\\D", ""))));
        }
    }
}
