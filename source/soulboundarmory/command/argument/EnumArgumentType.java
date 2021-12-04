package soulboundarmory.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.auoeke.reflect.Accessor;

public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T>{
    protected final Class<T> type;
    protected final List<T> values;

    protected EnumArgumentType(Class<T> type, Predicate<T> include) {
        this.type = type;

        var values = this.values = new ArrayList<>();

        for (var field : type.getDeclaredFields()) {
            var value = (T) Accessor.getObject(field);

            if (include.test(value)) {
                values.add(value);
            }
        }
    }

    protected EnumArgumentType(Class<T> enumClass) {
        this.type = enumClass;
        var values = this.values = new ArrayList<>();

        for (var field : enumClass.getDeclaredFields()) {
            //noinspection unchecked
            values.add((T) Accessor.getObject(field));
        }
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        return Enum.valueOf(this.type, reader.readString());
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
        return this.values;
    }
}
