package soulboundarmory.text.format;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.auoeke.reflect.Pointer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextFormatting;
import soulboundarmory.mixin.mixin.access.TextFormattingAccess;
import soulboundarmory.mixin.mixin.access.ColorAccess;
import soulboundarmory.util.Util;

public class FormattingRegistry {
    private static final Pointer pattern = new Pointer().staticField(TextFormatting.class, Util.mapField("field_96330_y"));
    private static final Pointer values = new Pointer().staticField(TextFormatting.class, "$VALUES");
    private static final Pointer colors = new Pointer().staticField(Color.class, Util.mapField("field_240738_a_"));

    private static final Map<String, TextFormatting> nameMap = TextFormattingAccess.nameMap();
    private static final Reference2ObjectOpenHashMap<TextFormatting, Color> colorMap = new Reference2ObjectOpenHashMap<>(ColorAccess.formattingColors());

    public static ExtendedFormatting register(String name, char code, int colorIndex, @Nullable Integer color) {
        return register(new ExtendedFormatting(name, code, colorIndex, color), code);
    }

    public static ExtendedFormatting register(String name, char code, boolean modifier) {
        return register(new ExtendedFormatting(name, code, modifier), code);
    }

    public static ExtendedFormatting register(String name, char code, boolean modifier, int colorIndex, @Nullable Integer color) {
        return register(new ExtendedFormatting(name, code, modifier, colorIndex, color), code);
    }

    private static ExtendedFormatting register(ExtendedFormatting formatting, char code) {
        if (Character.toString(code).toLowerCase(Locale.ROOT).charAt(0) != code) {
            throw new IllegalArgumentException(String.format("%s; uppercase codes are not allowed.", code));
        }

        if (TextFormatting.getByCode(code) != null) {
            throw new IllegalArgumentException(String.format("a Formatting with the code %s already exists.", code));
        }

        if (TextFormatting.getByName(formatting.cast().getName()) != null) {
            throw new IllegalArgumentException(String.format("a Formatting with name %s already exists.", formatting.cast().getName()));
        }

        var oldValues = (TextFormatting[]) values.getObject();
        var valueCount = oldValues.length;
        var newValues = Arrays.copyOf(oldValues, valueCount + 1);
        newValues[valueCount] = formatting.cast();
        values.putObject(newValues);

        nameMap.put(TextFormattingAccess.sanitize(formatting.cast().name()), formatting.cast());

        pattern.putObject(Pattern.compile(TextFormattingAccess.pattern().toString().replace("]", code + "]")));

        if (formatting.cast().isColor()) {
            colorMap.put(formatting.cast(), ColorAccess.instantiate(formatting.cast().getColor(), formatting.cast().getName()));
        }

        return formatting;
    }

    static {
        colors.putObject(colorMap);
    }
}
