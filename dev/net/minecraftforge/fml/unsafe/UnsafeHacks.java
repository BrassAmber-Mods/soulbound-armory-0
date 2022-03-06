package net.minecraftforge.fml.unsafe;

import java.lang.reflect.Field;
import net.auoeke.reflect.Accessor;
import net.auoeke.reflect.Fields;
import net.gudenau.lib.unsafe.Unsafe;

/**
 A reimplementation of {@code UnsafeHacks}.
 <p>
 Somewhy the original {@code UnsafeHacks} cannot be found.
 Perhaps because another dependency with artifact ID "unsafe" is present and overrides {@code net.minecraftforge:unsafe}?
 */
public class UnsafeHacks {
    public static Object newInstance(Class<?> type) {
        return Unsafe.allocateInstance(type);
    }

    public static Object getField(Field field, Object object) {
        return Accessor.get(object, field);
    }

    public static void setField(Field field, Object object, Object value) {
        Accessor.put(object, field, value);
    }

    public static int getIntField(Field field, Object object) {
        return Accessor.getInt(object, field);
    }

    public static void setIntField(Field field, Object object, int value) {
        Accessor.putInt(object, field, value);
    }

    public static void cleanEnumCache(Class<? extends Enum<?>> type) {
        var field = Fields.of(type, "enumConstantDirectory");

        if (field != null) {
            setField(field, type, null);
        }

        field = Fields.of(type, "enumConstants");

        if (field != null) {
            setField(field, type, null);
        }
    }
}
