package soulboundarmory.module.text.mixin;

import net.minecraft.client.resource.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.module.text.RomanNumerals;

@OnlyIn(Dist.CLIENT)
@Mixin(I18n.class)
abstract class I18nMixin {
	@Inject(method = "translate", at = @At("HEAD"), cancellable = true)
	private static void translate(String key, Object[] args, CallbackInfoReturnable<String> info) {
		var roman = RomanNumerals.fromDecimal(key);

		if (!roman.isEmpty()) {
			info.setReturnValue(roman);
		}
	}
}
