package soulboundarmory.text;

import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import net.auoeke.romeral.Numeral;
import net.auoeke.romeral.NumeralSystem;
import soulboundarmory.text.format.Formatting;

public final class RomanNumerals {
    private static final Long2ReferenceOpenHashMap<String> cache = new Long2ReferenceOpenHashMap<>();
    private static final NumeralSystem system = NumeralSystem.standard.with(
        StreamSupport.stream(Spliterators.spliterator(NumeralSystem.standard.listIterator(2), NumeralSystem.standard.size() - 2, 0), false)
            .flatMap(numeral -> IntStream.rangeClosed(0, 1).mapToObj(level -> Numeral.of(
                IntStream.rangeClosed(0, level)
                    .mapToObj(j -> "§" + Formatting.overlineCodes[j])
                    .collect(Collectors.joining("", "", numeral.roman() + "§r")),
                numeral.value() * IntMath.pow(1000, level + 1)
            )))
            .toArray(Numeral[]::new)
    );

    public static String fromDecimal(long decimal) {
        return cache.computeIfAbsent(decimal, system::toRoman);
    }
}
