package soulboundarmory.text.format;

import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import soulboundarmory.mixin.access.FontRenderer$CharacterRendererAccess;

/**
 A callback for applying custom formatting on each glyph in a text styled with a custom {@link Formatting}.
 */
@FunctionalInterface
@SuppressWarnings("JavadocReference")
public interface TextFormatter {
    /**
     This method is called when a glyph is drawn. Apply any custom effects here.

     @param drawer        the {@link TextRenderer.Drawer} instance that draws this glyph.
     @param style         the {@link Style} of the glyph.
     @param charIndex     the index of the current glyph in the text.
     @param character     the character.
     @param font          the current text's font.
     @param glyph         the current glyph.
     @param glyphRenderer the renderer for the current glyph.
     @param alpha         the alpha component of this glyph's color.
     @param red           the red component of this glyph's color.
     @param green         the green component of this glyph's color
     @param blue          the blue component of this glyph's color.
     @param advance       the width of this character.
     */
    void format(FontRenderer$CharacterRendererAccess drawer, Style style, int charIndex, int character, Font font, IGlyph glyph, TexturedGlyph glyphRenderer, Color color, float red, float green, float blue, float advance);
}
