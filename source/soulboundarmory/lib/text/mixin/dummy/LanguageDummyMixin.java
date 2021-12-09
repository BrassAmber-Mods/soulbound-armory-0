package soulboundarmory.lib.text.mixin.dummy;

import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @see Language
 */
@SuppressWarnings("unused")
@Mixin(targets = "net.minecraft.util.Language$1")
abstract class LanguageDummyMixin {}
