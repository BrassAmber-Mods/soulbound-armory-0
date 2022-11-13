package soulboundarmory.util;

import java.lang.annotation.Annotation;
import java.lang.ref.Reference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.jodah.typetools.TypeResolver;
import soulboundarmory.SoulboundArmory;

public class Util2 {
	public static IllegalArgumentException illegalArgument(String message, Object... arguments) {
		throw new IllegalArgumentException(message.formatted(arguments));
	}

	public static <A extends Annotation, T> T value(AnnotatedElement element, Function<? super A, ? extends T> getter, T fallback) {
		var annotation = element.getAnnotation((Class<A>) TypeResolver.resolveRawArguments(Function.class, getter.getClass())[0]);
		return annotation == null ? fallback : getter.apply(annotation);
	}

	public static <A extends Annotation, T> T value(AnnotatedElement element, Function<A, T> getter) {
		return value(element, getter, (T) null);
	}

	public static <A extends Annotation, T> T value(AnnotatedElement element, Function<? super A, ? extends T> getter, Supplier<? extends T> fallback) {
		var annotation = element.getAnnotation((Class<A>) TypeResolver.resolveRawArguments(Function.class, getter.getClass())[0]);
		return annotation == null ? fallback.get() : getter.apply(annotation);
	}

	public static <T, C extends Collection<? super T>> C add(C collection, T... things) {
		Collections.addAll(collection, things);
		return collection;
	}

	public static <T> Optional<T> or(Optional<T> optional, Supplier<? extends T> alternative) {
		return optional.or(() -> Optional.ofNullable(alternative.get()));
	}

	public static <T> T or(T t, Supplier<? extends T> alternative) {
		return t == null ? alternative.get() : t;
	}

	public static <T> boolean ifPresent(Optional<T> optional, Consumer<? super T> action) {
		if (optional.isPresent()) {
			action.accept(optional.get());

			return true;
		}

		return false;
	}

	public static <T> Iterable<T> iterate(Stream<T> stream) {
		return stream::iterator;
	}

	public static <T> Iterable<T> iterate(Iterator<T> iterator) {
		return () -> iterator;
	}

	public static <T> T[] fill(T[] array, Supplier<T> element) {
		for (var index = 0; index < array.length; ++index) {
			array[index] = element.get();
		}

		return array;
	}

	public static <T> T[][] fill2(T[][] array, Supplier<T> element) {
		for (var subarray : array) {
			fill(subarray, element);
		}

		return array;
	}

	public static <B> B nul(Object... vacuum) {
		return null;
	}

	public static <T> T cast(Object object) {
		return (T) object;
	}

	public static String capitalize(String string) {
		return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}

	public static <T> T[] array(T... elements) {
		return elements;
	}

	public static <T> List<T> list(T... elements) {
		return ReferenceArrayList.wrap(elements);
	}

	public static <K, V> Map<K, V> map(Map.Entry<K, V>... entries) {
		var map = new Reference2ReferenceLinkedOpenHashMap<K, V>();
		Stream.of(entries).forEach(entry -> map.put(entry.getKey(), entry.getValue()));

		return map;
	}

	public static <K, V, T extends Map<K, V>> T add(T map, Map.Entry<K, V> entry) {
		map.put(entry.getKey(), entry.getValue());
		return map;
	}

	public static boolean containsIgnoreCase(String string, String substring) {
		return Pattern.compile(substring, Pattern.CASE_INSENSITIVE | Pattern.LITERAL | Pattern.UNICODE_CASE).matcher(string).find();
	}

	public static boolean contains(Object target, Object... items) {
		return Arrays.asList(items).contains(target);
	}

	public static <T> Stream<T> stream(Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	public static <T> Class<T> componentType(T... array) {
		return (Class<T>) array.getClass().getComponentType();
	}

	public static <A> A[] add(A[] array, A element) {
		var union = Arrays.copyOf(array, array.length + 1);
		union[array.length] = element;

		return union;
	}

	public static <A> A[] add(A element, A[] array) {
		var union = (A[]) Array.newInstance(componentType(array), array.length + 1);
		System.arraycopy(array, 0, union, 1, array.length);
		union[0] = element;

		return union;
	}

	public static <T> void each(Iterable<? extends Reference<? extends T>> iterable, Consumer<? super T> action) {
		var iterator = iterable.iterator();

		while (iterator.hasNext()) {
			var reference = iterator.next();

			if (reference == null) {
				SoulboundArmory.logger.error("🤨 Something's fishy.");
			} else if (!reference.refersTo(null)) {
				action.accept(reference.get());

				continue;
			}

			try {
				iterator.remove();
			} catch (IndexOutOfBoundsException __) {
				SoulboundArmory.logger.error("Something is very fishy.");
			}
		}
	}
}
