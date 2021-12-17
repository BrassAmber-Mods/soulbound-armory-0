package cell.client.gui.widget;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.text.Text;
import soulboundarmory.util.Math2;
import soulboundarmory.util.Util;

public class TextWidget extends Widget<TextWidget> {
    public List<Text> text = new ObjectArrayList<>();
    public boolean shadow;
    public int color;
    public int stroke;

    @Override
    public TextWidget text(Text text) {
        this.text.add(text);

        return this;
    }

    public TextWidget text(Iterable<Text> text) {
        text.forEach(this::text);

        return this;
    }

    public TextWidget shadow(boolean shadow) {
        this.shadow = shadow;

        return this;
    }

    public TextWidget shadow() {
        return this.shadow(true);
    }

    public TextWidget color(int color) {
        this.color = color;

        return this;
    }

    public TextWidget stroke(int color) {
        this.stroke = color;

        return this;
    }

    public TextWidget stroke() {
        return this.stroke(0xFF000000);
    }

    public int color() {
        return this.adjustColor(this.color, 0xFFFFFFFF);
    }

    public int strokeColor() {
        return this.adjustColor(this.stroke, 0xFF000000);
    }

    @Override
    public int width() {
        return this.text.stream().map(textRenderer::getWidth).max(Comparator.naturalOrder()).orElse(0);
    }

    @Override
    public int height() {
        return fontHeight() * this.text.size();
    }

    protected int adjustColor(int color, int fallback) {
        if (color == 0) {
            color = fallback;
        }

        if ((color & 0xFF000000) == 0) {
            color |= 0xFF000000;
        }

        if (!this.active()) {
            color = color & 0xFFFFFF | Math2.alpha(color) * 160 / 255 << 24;
        }

        return color;
    }

    @Override
    protected void render() {
        Util.enumerate(this.text, (text, row) -> {
            var y = this.y() + fontHeight() * row;

            if (this.stroke != 0) {
                drawStrokedText(this.matrixes, text, this.x(), y, this.color(), this.strokeColor());
            }

            if (this.shadow) {
                textRenderer.drawWithShadow(this.matrixes, text, this.x(), y, this.color());
            } else {
                textRenderer.draw(this.matrixes, text, this.x(), y, this.color());
            }
        });
    }
}
