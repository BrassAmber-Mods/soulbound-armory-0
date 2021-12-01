package soulboundarmory.text.format;

import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import soulboundarmory.mixin.access.FontRenderer$CharacterRendererAccess;

public class OverlineFormatter implements TextFormatter {
    public final int yMultiplier;

    public OverlineFormatter(int level) {
        this.yMultiplier = level;
    }

    @Override
    public void format(
        FontRenderer$CharacterRendererAccess drawer,
        Style style,
        int charIndex,
        int character,
        Font storage,
        IGlyph glyph,
        TexturedGlyph glyphRenderer,
        Color color,
        float red,
        float green,
        float blue,
        float advance
    ) {
        var shadow = drawer.shadow() ? 1 : 0;
        var x = drawer.x() + shadow;
        var y = drawer.y() + shadow - 1 - 2 * this.yMultiplier;

        drawer.invokeAddRectangle(new TexturedGlyph.Effect(x - 1, y, x + advance, y - 1, 0.01F, red, green, blue, drawer.a()));
    }
}
