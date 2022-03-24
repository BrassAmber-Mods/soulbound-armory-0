package soulboundarmory.module.config;

import java.lang.reflect.Field;
import net.auoeke.reflect.Pointer;
import soulboundarmory.util.Util;

public final class ConfigurationField<T> extends ConfigurationNode {
    public final ConfigurationNode parent;
    public final String comment;
    public final T defaultValue;

    private final Pointer field;

    public ConfigurationField(ConfigurationNode parent, Field field) {
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
