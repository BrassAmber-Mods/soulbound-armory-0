package soulboundarmory.lib.gui.widget;

import java.util.Comparator;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.text.Text;
import soulboundarmory.function.ObjectSupplier;
import soulboundarmory.util.Math2;
import soulboundarmory.util.Util;

public class TextWidget extends Widget<TextWidget> {
    public List<Supplier<Text>> text = new ReferenceArrayList<>();
    public IntSupplier color = () -> 0xFFFFFFFF;
    public boolean shadow;
    public int stroke;
    public boolean hasStroke;

    @Override
    public TextWidget text(Text text) {
        this.text.add(() -> text);

        return this;
    }

    public TextWidget text(Iterable<Text> text) {
        text.forEach(this::text);

        return this;
    }

    @Override
    public TextWidget text(String text) {
        return this.text(Text.of(text));
    }

    public TextWidget text(Supplier<Text> text) {
        this.text.add(text);

        return this;
    }

    public TextWidget text(ObjectSupplier text) {
        this.text.add(() -> Text.of(String.valueOf(text.get())));

        return this;
    }

    public TextWidget overwrite(Text text) {
        this.text.clear();

        return this.text(text);
    }

    public TextWidget overwrite(Iterable<Text> text) {
        this.text.clear();

        return this.text(text);
    }

    public TextWidget overwrite(String text) {
        this.text.clear();

        return this.text(text);
    }

    public TextWidget overwrite(Supplier<Text> text) {
        this.text.clear();

        return this.text(text);
    }

    public TextWidget overwrite(ObjectSupplier text) {
        this.text.clear();

        return this.text(text);
    }

    public TextWidget shadow(boolean shadow) {
        this.shadow = shadow;

        return this;
    }

    public TextWidget shadow() {
        return this.shadow(true);
    }

    public TextWidget color(IntSupplier color) {
        this.color = color;

        return this;
    }

    public TextWidget color(int color) {
        this.color = () -> color;

        return this;
    }

    public TextWidget stroke(int color) {
        this.stroke = color;
        this.hasStroke = true;

        return this;
    }

    public TextWidget stroke() {
        return this.stroke(0xFF000000);
    }

    public TextWidget stroke(boolean stroke) {
        this.hasStroke = stroke;

        return this;
    }

    public Stream<Text> text() {
        return this.text.stream().map(Supplier::get);
    }

    public int color() {
        return this.adjustColor(this.color.getAsInt(), 0xFFFFFFFF);
    }

    public int strokeColor() {
        return this.hasStroke ? this.adjustColor(this.stroke, 0xFF000000) : 0;
    }

    @Override
    public int width() {
        var width = this.text().map(textRenderer::getWidth).max(Comparator.naturalOrder()).orElse(0);

        if (this.hasStroke) {
            width += 2;
        } else if (this.shadow) {
            width++;
        }

        return width;
    }

    @Override
    public int height() {
        return fontHeight() * (int) this.text().count();
    }

    protected int adjustColor(int color, int fallback) {
        if (color == 0) {
            color = fallback;
        }

        if ((color & 0xFF000000) == 0) {
            color |= 0xFF000000;
        }

        if (!this.isActive()) {
            color = color & 0xFFFFFF | Math2.alpha(color) * 160 / 255 << 24;
        }

        return color;
    }

    @Override
    protected void render() {
        this.matrixes.push();
        this.matrixes.translate(0, 0, this.z());

        this.withZ(() -> Util.enumerate(this.text().toList(), (text, row) -> {
            var y = this.y() + fontHeight() * row;

            if (this.hasStroke) {
                drawStrokedText(this.matrixes, text, this.x(), y, this.color(), this.strokeColor());
            }

            if (this.shadow) {
                textRenderer.drawWithShadow(this.matrixes, text, this.x(), y, this.color());
            } else {
                textRenderer.draw(this.matrixes, text, this.x(), y, this.color());
            }
        }));

        this.matrixes.pop();
    }
}
