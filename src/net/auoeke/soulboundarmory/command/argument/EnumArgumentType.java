package net.auoeke.soulboundarmory.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.auoeke.soulboundarmory.SoulboundArmory;

public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T>{
    protected final Class<T> clazz;
    protected final List<T> values;

    protected EnumArgumentType(Class<T> enumClass, Predicate<T> include) {
        this.clazz = enumClass;

         List<T> values = this.values = new ArrayList<>();

        for (Field field : enumClass.getDeclaredFields()) {
            try {
                //noinspection unchecked
                 T value = (T) field.get(null);
                if (include.test(value)) {
                    values.add(value);
                }
            } catch (IllegalAccessException exception) {
                SoulboundArmory.logger.error(String.format("Cannot access enum %s:", field.getName()), exception);
            }
        }
    }

    protected EnumArgumentType(Class<T> enumClass) {
        this.clazz = enumClass;
         List<T> values = this.values = new ArrayList<>();

        for (Field field : enumClass.getDeclaredFields()) {
            try {
                //noinspection unchecked
                values.add((T) field.get(null));
            } catch (IllegalAccessException exception) {
                SoulboundArmory.logger.error(String.format("Cannot access enum %s:", field.getName()), exception);
            }
        }
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        return Enum.valueOf(this.clazz, reader.readString());
    }

    public static <T extends Enum<T>> EnumArgumentType<T> enumeration(Class<T> enumClass) {
        return new EnumArgumentType<>(enumClass);
    }

    public static <T extends Enum<T>> EnumArgumentType<T> include(Class<T> enumClass, Predicate<T> include) {
        return new EnumArgumentType<>(enumClass, include);
    }

    public static <T extends Enum<T>> EnumArgumentType<T> exclude(Class<T> enumClass, Predicate<T> exclude) {
        return new EnumArgumentType<>(enumClass, exclude.negate());
    }

    public List<T> getValues() {

        return values;
    }
}
