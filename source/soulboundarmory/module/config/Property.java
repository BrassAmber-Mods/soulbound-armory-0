package soulboundarmory.module.config;

import java.lang.reflect.Field;
import net.auoeke.reflect.Pointer;
import soulboundarmory.util.Util;

public final class Property<T> extends Node {
    public final Parent parent;
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
        this.comment = Util.value(field, (Comment comment) -> String.join("\n", comment.value()));
        this.field = Pointer.of(field);
        this.defaultValue = this.get();

        var interval = field.getAnnotation(Interval.class);

        if (interval != null && field.getType() != int.class) {
            throw new ClassCastException("@Interval field %s.%s must be of type int".formatted(parent.type.getName(), field.getName()));
        }
    }

    public T get() {
        return (T) this.field.get();
    }

    public void set(Object value) {
        this.field.put(value);
    }

    public void reset() {
        this.set(this.defaultValue);
    }
}
