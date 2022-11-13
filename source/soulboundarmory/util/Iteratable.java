package soulboundarmory.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public record Iteratable<T>(Iterator<T> iterator) implements Iterable<T>, Iterator<T> {
	public static <T> Iteratable<T> of(Iterator<T> iterator) {
		return new Iteratable<>(iterator);
	}

	public static <T> Iteratable<T> of(List<T> iterable) {
		return new Iteratable<>(iterable.listIterator());
	}

	public static <T> Iteratable<T> of(Iterable<T> iterable) {
		return new Iteratable<>(iterable.iterator());
	}

	@Override public boolean hasNext() {
		return this.iterator.hasNext();
	}

	@Override public T next() {
		return this.iterator.next();
	}

	@Override public void remove() {
		this.iterator.remove();
	}

	@Override public void forEachRemaining(Consumer<? super T> action) {
		this.iterator.forEachRemaining(action);
	}

	@Override public void forEach(Consumer<? super T> action) {
		this.iterator.forEachRemaining(action);
	}
}
