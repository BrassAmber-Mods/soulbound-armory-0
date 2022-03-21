package soulboundarmory.lib.gui.widget;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import soulboundarmory.function.ObjectSupplier;
import soulboundarmory.util.Math2;
import soulboundarmory.util.Util;

public class TextWidget extends Widget<TextWidget> {
    public List<Supplier<? extends Text>> text = new ReferenceArrayList<>();
    public IntSupplier color = () -> 0xFFFFFFFF;
    public boolean shadow;
    public int stroke;
    public boolean hasStroke;

    @Override public TextWidget text(Supplier<? extends Text> text) {
        this.text.add(text);

        return this;
    }

    @Override public TextWidget text(Consumer<? super TextWidget> configure) {
        return this.with(configure);
    }

    public TextWidget text(StringVisitable text) {
        return this.text(text::getString);
    }

    public TextWidget text(ObjectSupplier text) {
        this.text.add(() -> Text.of(String.valueOf(text.get())));

        return this;
    }

    public TextWidget overwrite(Text text) {
        this.text.clear();

        return this.text(text);
    }

    public TextWidget overwrite(Iterable<? extends Text> text) {
        this.text.clear();

        return this.text(text);
    }

    public TextWidget overwrite(String text) {
        this.text.clear();

        return this.text(text);
    }

    public TextWidget overwrite(Supplier<? extends Text> text) {
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

    public TextWidget color(Formatting formatting) {
        return this.color(formatting.getColorValue());
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
        return this.adjustSize(this.text().mapToInt(textRenderer::getWidth).max().orElse(0));
    }

    @Override
    public int height() {
        return this.adjustSize(fontHeight() * (int) this.text().count());
    }

    @Override
    protected void render() {
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

            if (this.isFocused() && this.isActive()) {
                drawStrokedText(this.matrixes, text, this.x(), y, this.strokeColor(), this.color());
            }
        }));
    }

    protected int adjustColor(int color, int fallback) {
        if (color == 0) color = fallback;
        if ((color & 0xFF000000) == 0) color |= 0xFF000000;
        if (!this.isActive()) color = color & 0xFFFFFF | Math2.alpha(color) * 160 / 255 << 24;

        return color;
    }

    protected int adjustSize(int baseSize) {
        if (this.hasStroke) {
            baseSize += 2;
        } else if (this.shadow) {
            baseSize++;
        }

        return baseSize;
    }
}
