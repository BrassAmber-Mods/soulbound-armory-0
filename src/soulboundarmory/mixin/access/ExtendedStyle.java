package soulboundarmory.mixin.access;

import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public interface ExtendedStyle {
    Set<TextFormatting> formattings();

    boolean has(TextFormatting formatting);

    void add(TextFormatting formatting);

    void add(Style to);

    default Style cast() {
        return (Style) this;
    }

    default void add(Iterable<TextFormatting> formattings) {
        formattings.forEach(this::add);
    }

    default void add(TextFormatting... formattings) {
        Stream.of(formattings).forEach(this::add);
    }
}
