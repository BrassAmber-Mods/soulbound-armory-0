package soulboundarmory.module.text.mixin;

import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.module.text.ColorFunction;
import soulboundarmory.module.text.ExtendedFormatting;

@Mixin(TextColor.class)
abstract class TextColorMixin {
	@Shadow
	@Final
	private int rgb;

	private int phormat_previousColor = this.rgb;

	private boolean phormat_hasColorFunction;

	private ColorFunction phormat_colorFunction;

	@Inject(method = "fromFormatting", at = @At("RETURN"), cancellable = true)
	private static void setColorFunction(Formatting formatting, CallbackInfoReturnable<TextColor> info) {
		if ((Object) formatting instanceof ExtendedFormatting extendedFormatting) {
			var color = (TextColorMixin) (Object) info.getReturnValue();
			color.phormat_colorFunction = extendedFormatting.colorFunction();
			color.phormat_hasColorFunction = color.phormat_colorFunction != null;
		}
	}

	@Inject(method = "getRgb", at = @At("HEAD"), cancellable = true)
	public void fix(CallbackInfoReturnable<Integer> info) {
		if (this.phormat_hasColorFunction) {
			info.setReturnValue(this.phormat_previousColor = this.phormat_colorFunction.apply(this.phormat_previousColor));
		}
	}

	@Inject(method = "getHexCode", at = @At("HEAD"), cancellable = true)
	public void matchHexCode(CallbackInfoReturnable<String> info) {
		if (this.phormat_hasColorFunction) {
			info.setReturnValue(Integer.toHexString(this.phormat_previousColor));
		}
	}
}
