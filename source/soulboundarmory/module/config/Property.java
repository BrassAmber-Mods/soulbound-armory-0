package soulboundarmory.module.config;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.stream.Stream;
import com.sun.jdi.InvalidTypeException;
import net.auoeke.reflect.Pointer;
import net.gudenau.lib.unsafe.Unsafe;
import soulboundarmory.util.Util;

public final class Property<T> extends Node {
    public final Parent parent;
    public final Class<?> type;
    public final String comment;
    public final T defaultValue;

    private final Pointer field;

    public Property(Parent parent, Field field) {
        super(field.getName(), Util.value(field, (Category category) -> {
            if (ConfigurationFile.class.isAssignableFrom(field.getDeclaringClass())) {
                return category.value();
            }

            throw new IllegalArgumentException("@Category found on field %s.%s below the top level".formatted(field.getDeclaringClass().getName(), field.getName()));
        }, parent.category));

        this.parent = parent;
        this.type = field.getType();
        this.comment = Util.value(field, (Comment comment) -> String.join("\n", comment.value()));
        this.field = Pointer.of(field);
        this.defaultValue = this.get();

        var interval = field.getAnnotation(Interval.class);

        if (interval != null && field.getType() != int.class) {
            Unsafe.throwException(new InvalidTypeException("@Interval field %s.%s must be of type int".formatted(parent.type.getName(), field.getName())));
        }

        var quotientInterval = field.getAnnotation(QuotientInterval.class);

        if (quotientInterval != null && field.getType() != double.class) {
            Unsafe.throwException(new InvalidTypeException("@Interval field %s.%s must be of type int".formatted(parent.type.getName(), field.getName())));
        }
    }

    public Entry<?> entry() {
        return (Entry<?>) Stream.iterate(this.parent, Objects::nonNull, parent -> parent instanceof Group group ? group.parent : null).reduce(this.parent, (a, b) -> b);
    }

    public T get() {
        return (T) this.field.get();
    }

    public void set(Object value) {
        this.field.put(value);
        this.entry().desynced();
    }

    public void reset() {
        this.set(this.defaultValue);
    }

    @Override public String toString() {
        return this.name + " = " + this.get();
    }
}
