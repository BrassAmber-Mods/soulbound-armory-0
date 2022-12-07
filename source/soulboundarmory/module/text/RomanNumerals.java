package soulboundarmory.module.text;

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
	 Translates an enchantment level key into an extended Roman number with
	 {@link FormattingExtensions#overlineCodes custom formatting codes} for up to 2 rows of overlines.
	 The level must fit in a {@code long} and be greater than {@link Long#MIN_VALUE}.

	 @param key an echantment level key
	 @return the level's representation in Roman numerals
	 @throws NumberFormatException if the level does not fit in a {@code long}
	 @throws StackOverflowError if the level is {@link Long#MIN_VALUE}
	 */
	public static String fromDecimal(String key) {
		return cache.computeIfAbsent(key, (String k) -> {
			var matcher = Pattern.compile("(?<=enchantment\\.level\\.)\\d+").matcher(k);
			return matcher.find() ? system.toRoman(Long.parseLong(matcher.group())) : "";
		});
	}
}
