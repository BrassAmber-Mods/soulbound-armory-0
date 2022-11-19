package soulboundarmory.module.text.mixin;

import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Formatting.class)
abstract class FormattingMixin {
	@Redirect(method = "sanitize", at = @At(value = "INVOKE", target = "java/lang/String.replaceAll(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"))
	private static String replaceNone(String string, String regex, String replacement) {
		return string;
	}
}
