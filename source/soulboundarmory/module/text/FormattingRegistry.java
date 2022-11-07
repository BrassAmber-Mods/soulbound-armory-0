package soulboundarmory.module.text;

import java.util.Arrays;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.auoeke.reflect.Pointer;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import soulboundarmory.mixin.MixinUtil;

public class FormattingRegistry {
	private static final Pointer values = Pointer.of(Formatting.class, MixinUtil.formattingValueField);

	public static ExtendedFormatting register(String name, char code, int colorIndex, @Nullable Integer color) {
		return register(new ExtendedFormatting(name, code, colorIndex, color));
	}

	public static ExtendedFormatting register(String name, char code, boolean modifier) {
		return register(new ExtendedFormatting(name, code, modifier));
	}

	public static ExtendedFormatting register(String name, char code, boolean modifier, int colorIndex, @Nullable Integer color) {
		return register(new ExtendedFormatting(name, code, modifier, colorIndex, color));
	}

	private static ExtendedFormatting register(ExtendedFormatting formatting) {
		if (Character.toLowerCase(formatting.code()) != formatting.code()) {
			throw new IllegalArgumentException(String.format("%s; uppercase codes are not allowed.", formatting.code()));
		}

		if (Formatting.byCode(formatting.code()) != null) {
			throw new IllegalArgumentException(String.format("a Formatting with the code %s already exists.", formatting.code()));
		}

		var cast = formatting.cast();

		if (Formatting.byName(cast.getName()) != null) {
			throw new IllegalArgumentException(String.format("a Formatting with name %s already exists.", cast.getName()));
		}

		Formatting[] oldValues = values.getT();
		var valueCount = oldValues.length;
		var newValues = Arrays.copyOf(oldValues, valueCount + 1);
		newValues[valueCount] = cast;
		values.putReference(newValues);

		Formatting.BY_NAME.put(Formatting.sanitize(cast.name()), cast);
		Formatting.FORMATTING_CODE_PATTERN = Pattern.compile(Formatting.FORMATTING_CODE_PATTERN.toString().replace("]", formatting.code() + "]"));

		if (cast.isColor()) {
			TextColor.FORMATTING_TO_COLOR.put(cast, new TextColor(cast.getColorValue(), cast.getName()));
		}

		return formatting;
	}

	static {
		TextColor.FORMATTING_TO_COLOR = new Reference2ObjectOpenHashMap<>(TextColor.FORMATTING_TO_COLOR);
	}
}
