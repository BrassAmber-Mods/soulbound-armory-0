package soulboundarmory.module.text;

import java.util.Map;
import java.util.Spliterators;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.auoeke.romeral.Numeral;
import net.auoeke.romeral.NumeralSystem;

public final class RomanNumerals {
    private static final Object2ReferenceOpenHashMap<String, String> cache = new Object2ReferenceOpenHashMap<>();
    private static final NumeralSystem system = NumeralSystem.standard.with(
        StreamSupport.stream(Spliterators.spliterator(NumeralSystem.standard.listIterator(2), NumeralSystem.standard.size() - 2, 0), false)
            .flatMap(numeral -> IntStream.rangeClosed(0, 1).mapToObj(level -> Numeral.of(
                IntStream.rangeClosed(0, level)
                    .mapToObj(j -> "§" + FormattingExtensions.overlineCodes[j])
                    .collect(Collectors.joining("", "", numeral.roman() + "§r")),
                numeral.value() * IntMath.pow(1000, level + 1)
            )))
            .toArray(Numeral[]::new)
    );

	/**
	 Converts a decimal number into an extended Roman number with
	 {@link FormattingExtensions#overlineCodes custom formatting codes} for up to 2 rows of overlines.
	 The decimal number must fit in a {@code long} and be greater than {@link Long#MIN_VALUE}.

	 @implNote
	 {@link Map#computeIfAbsent} ignores {@code null} values.

	 @param decimal a string representing a decimal number
	 @return {@code decimal}'s representation in Roman numerals
	 @throws NumberFormatException if the decimal number does not fit in a {@code long}
	 @throws StackOverflowError if the decimal number is {@link Long#MIN_VALUE}
	 */
	public static String fromDecimal(String decimal) {
		if (cache.containsKey(decimal)) {
			return cache.get(decimal);
		}

		var matcher = Pattern.compile("(?<=enchantment\\.level\\.)\\d+").matcher(decimal);
		var value = matcher.find() ? system.toRoman(Long.parseLong(matcher.group())) : null;
		cache.put(decimal, value);

		return value;
	}
}
