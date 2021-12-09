package soulboundarmory.lib.text;

import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import soulboundarmory.lib.text.access.FontRenderer$DrawerAccess;

public class OverlineFormatter implements TextFormatter {
    public final int yMultiplier;

    public OverlineFormatter(int level) {
        this.yMultiplier = level;
    }

    @Override
    public void format(
        FontRenderer$DrawerAccess drawer,
        Style style,
        int charIndex,
        int character,
        FontStorage storage,
        Glyph glyph,
        GlyphRenderer glyphRenderer,
        TextColor color,
        float red,
        float green,
        float blue,
        float advance
    ) {
        var shadow = drawer.shadow() ? 1 : 0;
        var x = drawer.x() + shadow;
        var y = drawer.y() + shadow - 1 - 2 * this.yMultiplier;

        drawer.invokeAddRectangle(new GlyphRenderer.Rectangle(x - 1, y, x + advance, y - 1, 0.01F, red, green, blue, drawer.a()));
    }
}
