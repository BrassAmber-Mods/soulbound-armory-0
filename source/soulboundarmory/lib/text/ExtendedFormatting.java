package soulboundarmory.lib.text;

import javax.annotation.Nullable;
import net.minecraft.util.Formatting;

public class ExtendedFormatting /*extends Formatting*/ {
    public static final String VALUES = "field_1072";

    public ColorFunction colorFunction;
    public TextFormatter formatter;

    protected ExtendedFormatting(String name, char code, int colorIndex, @Nullable Integer colorValue) {}

    protected ExtendedFormatting(String name, char code, boolean modifier) {}

    protected ExtendedFormatting(String name, char code, boolean modifier, int colorIndex, @Nullable Integer colorValue) {}

    public Formatting cast() {
        return (Formatting) (Object) this;
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
