package soulboundarmory.lib.text;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.auoeke.reflect.Pointer;
import net.minecraft.util.Formatting;
import net.minecraft.text.TextColor;
import soulboundarmory.lib.text.mixin.access.TextColorAccess;
import soulboundarmory.lib.text.mixin.access.FormattingAccess;
import soulboundarmory.util.Util;

public class FormattingRegistry {
    private static final Pointer pattern = new Pointer().staticField(Formatting.class, Util.mapField("field_96330_y"));
    private static final Pointer values = new Pointer().staticField(Formatting.class, ExtendedFormatting.VALUES);
    private static final Pointer colors = new Pointer().staticField(TextColor.class, Util.mapField("field_240738_a_"));

    private static final Map<String, Formatting> nameMap = FormattingAccess.nameMap();
    private static final Reference2ObjectOpenHashMap<Formatting, TextColor> colorMap = new Reference2ObjectOpenHashMap<>(TextColorAccess.formattingColors());

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

        if (Formatting.byCode(code) != null) {
            throw new IllegalArgumentException(String.format("a Formatting with the code %s already exists.", code));
        }

        if (Formatting.byName(formatting.cast().getName()) != null) {
            throw new IllegalArgumentException(String.format("a Formatting with name %s already exists.", formatting.cast().getName()));
        }

        var oldValues = (Formatting[]) values.getObject();
        var valueCount = oldValues.length;
        var newValues = Arrays.copyOf(oldValues, valueCount + 1);
        newValues[valueCount] = formatting.cast();
        values.putObject(newValues);

        nameMap.put(FormattingAccess.sanitize(formatting.cast().name()), formatting.cast());

        pattern.putObject(Pattern.compile(pattern.getObject().toString().replace("]", code + "]")));

        if (formatting.cast().isColor()) {
            colorMap.put(formatting.cast(), TextColorAccess.instantiate(formatting.cast().getColorValue(), formatting.cast().getName()));
        }

        return formatting;
    }

    static {
        colors.putObject(colorMap);
    }
}
