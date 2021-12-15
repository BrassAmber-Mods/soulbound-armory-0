package soulboundarmory.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.auoeke.reflect.Accessor;
import net.auoeke.reflect.Fields;

public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T>{
    protected final Class<T> type;
    protected final List<T> values;

    protected EnumArgumentType(Class<T> type, Predicate<? super T> include) {
        this.type = type;
        this.values = new ArrayList<>();

        Fields.of(type).map(field1 -> (T) Accessor.getReference(field1)).filter(include).forEach(this.values::add);
    }

    protected EnumArgumentType(Class<T> type) {
        this.type = type;
        this.values = new ArrayList<>();

        Fields.of(type).forEach(field -> this.values.add((T) Accessor.getReference(field)));
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
