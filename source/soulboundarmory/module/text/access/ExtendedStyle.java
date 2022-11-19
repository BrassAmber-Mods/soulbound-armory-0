package soulboundarmory.module.text.access;

import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public interface ExtendedStyle {
	Set<Formatting> formattings();

	boolean has(Formatting formatting);

	void add(Formatting formatting);

	void add(Style to);

	default Style cast() {
		return (Style) this;
	}

	default void add(Iterable<Formatting> formattings) {
		formattings.forEach(this::add);
	}

	default void add(Formatting... formattings) {
		Stream.of(formattings).forEach(this::add);
	}
}
