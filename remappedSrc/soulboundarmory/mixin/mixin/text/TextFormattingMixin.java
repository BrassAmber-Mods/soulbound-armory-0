package soulboundarmory.mixin.mixin.text;

import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import soulboundarmory.mixin.mixin.access.TextFormattingAccess;

@Mixin(Formatting.class)
abstract class TextFormattingMixin implements TextFormattingAccess {
    @Redirect(method = "cleanName",
              at = @At(value = "INVOKE",
                       target = "java/lang/String.replaceAll(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"))
    private static String replaceNone(String string, String regex, String replacement) {
        return string;
    }
}
