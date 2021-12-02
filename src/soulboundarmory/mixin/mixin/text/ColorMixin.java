package soulboundarmory.mixin.mixin.text;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.text.format.ColorFunction;
import soulboundarmory.text.format.ExtendedFormatting;

@Mixin(Color.class)
abstract class ColorMixin {
    @Shadow
    @Final
    private int value;

    @SuppressWarnings("FieldMayBeFinal") // mutated via handwritten bytecode
    private int phormat_previousColor = this.value;

    private boolean phormat_hasColorFunction;

    @SuppressWarnings({"unused", "RedundantSuppression"})
    private ColorFunction phormat_colorFunction;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "fromLegacyFormat", at = @At("RETURN"), cancellable = true)
    private static void setColorFunction(TextFormatting formatting, CallbackInfoReturnable<Color> info) {
        if ((Object) formatting instanceof ExtendedFormatting extendedFormatting) {
            var color = (ColorMixin) (Object) info.getReturnValue();
            color.phormat_colorFunction = extendedFormatting.colorFunction();
            color.phormat_hasColorFunction = color.phormat_colorFunction != null;
        }
    }

    @Inject(method = "formatValue", at = @At("HEAD"), cancellable = true)
    public void matchHexCode(CallbackInfoReturnable<String> info) {
        if (this.phormat_hasColorFunction) {
            info.setReturnValue(Integer.toHexString(this.phormat_previousColor));
        }
    }
}
