package soulboundarmory.text.format;

import javax.annotation.Nullable;
import net.minecraft.util.text.TextFormatting;

public class ExtendedFormatting /*extends TextFormatting*/ {
    public ColorFunction colorFunction;
    public TextFormatter formatter;

    protected ExtendedFormatting(String name, char code, int colorIndex, @Nullable Integer colorValue) {}

    protected ExtendedFormatting(String name, char code, boolean modifier) {}

    protected ExtendedFormatting(String name, char code, boolean modifier, int colorIndex, @Nullable Integer colorValue) {}

    public TextFormatting cast() {
        return (TextFormatting) (Object) this;
    }

    // @Override
    @Nullable
    public Integer getColor() {
        var color = this.super$getColor();

        if (this.colorFunction != null && color != null) {
            return this.colorFunction.apply(color);
        }

        return color;
    }

    public ColorFunction colorFunction() {
        return this.colorFunction;
    }

    public ExtendedFormatting colorFunction(ColorFunction colorFunction) {
        this.colorFunction = colorFunction;

        return this;
    }

    public TextFormatter formatter() {
        return this.formatter;
    }

    public ExtendedFormatting formatter(TextFormatter formatter) {
        this.formatter = formatter;

        return this;
    }

    public native char code();

    private native Integer super$getColor();
}
