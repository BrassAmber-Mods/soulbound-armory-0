package soulboundarmory.module.config;

import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.stream.Stream;
import net.auoeke.reflect.Pointer;
import soulboundarmory.util.Util2;

public final class Property<T> extends Node {
	public final Parent parent;
	public final Class<?> type;
	public final String comment;
	public final T defaultValue;
	public final Interval interval;

	private final Pointer field;

	public Property(Parent parent, Field field) {
		super(Util2.value(field, Name::value, field.getName()), Util2.value(field, (Category category) -> {
			if (field.getDeclaringClass().isAnnotationPresent(ConfigurationFile.class)) {
				return category.value();
			}

			throw new IllegalArgumentException("@Category found on field %s.%s below the top level".formatted(field.getDeclaringClass().getName(), field.getName()));
		}, parent.category));

		this.parent = parent;
		this.type = field.getType();
		this.comment = Util2.value(field, (Comment comment) -> String.join("\n", comment.value()));
		this.field = Pointer.of(field);
		this.defaultValue = this.get();

		this.interval = field.getAnnotation(Interval.class);

		if (this.interval != null && field.getType() != int.class) {
			throw new InvalidClassException("@Interval field %s.%s must be of type int".formatted(parent.type.getName(), field.getName()));
		}

		var quotientInterval = field.getAnnotation(QuotientInterval.class);

		if (quotientInterval != null && field.getType() != double.class) {
			throw new InvalidClassException("@QuotientInterval field %s.%s must be of type double".formatted(parent.type.getName(), field.getName()));
		}
	}

	public ConfigurationInstance configuration() {
		return (ConfigurationInstance) Stream.iterate(this.parent, Objects::nonNull, parent -> parent instanceof Group group ? group.parent : null).reduce(this.parent, (a, b) -> b);
	}

	public T get() {
		return (T) this.field.get();
	}

	public void set(Object value) {
		this.field.put(value);
		this.configuration().desynced();
	}

	public void reset() {
		this.set(this.defaultValue);
	}

	@Override public String toString() {
		return this.name + " = " + this.get();
	}
}
