package net.auoeke.soulboundarmory.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.auoeke.reflect.Accessor;
import net.auoeke.soulboundarmory.SoulboundArmory;
import net.minecraft.command.ISuggestionProvider;

public class ConstantArgumentType<T> implements ArgumentType<List<T>> {
    protected final Class<T> clazz;
    protected final Map<String, Field> validFields;

    protected ConstantArgumentType(Class<T> clazz) {
        this.clazz = clazz;
        this.validFields = new LinkedHashMap<>();
    }

    public static <T> List<T> getConstants(CommandContext<?> context, String name, Class<T> type) {
        //noinspection unchecked
        return (List<T>) context.getArgument(name, List.class);
    }

    public static <T> ConstantArgumentType<T> allConstants(Class<T> clazz) {
        var type = new ConstantArgumentType<>(clazz);

        for (var field : type.clazz.getDeclaredFields()) {
            var modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                type.validFields.put(field.getName(), field);
            }
        }

        return type;
    }

    public static <T, U> ConstantArgumentType<T> allConstants(Class<T> holderClass, Class<U> fieldClass) {
        var type = new ConstantArgumentType<>(holderClass);

        for (var field : type.clazz.getDeclaredFields()) {
            var modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && fieldClass.isInstance(Accessor.get(field))) {
                type.validFields.put(field.getName(), field);
            }
        }

        return type;
    }

    @SafeVarargs
    public static <T> ConstantArgumentType<T> excludeConstants(Class<T> clazz, T... values) {
        return excludeConstants(clazz, Arrays.asList(values));
    }

    @SafeVarargs
    public static <T> ConstantArgumentType<T> includeConstants(Class<T> clazz, T... values) {
        return includeConstants(clazz, Arrays.asList(values));
    }

    public static <T> ConstantArgumentType<T> excludeConstants(Class<T> clazz, List<T> values) {
        return excludeConstants(clazz, values::contains);
    }

    public static <T> ConstantArgumentType<T> includeConstants(Class<T> clazz, List<T> values) {
        return includeConstants(clazz, values::contains);
    }

    public static <T> ConstantArgumentType<T> excludeConstants(Class<T> clazz, Predicate<T> exclude) {
        return includeConstants(clazz, exclude.negate());
    }

    public static <T> ConstantArgumentType<T> includeConstants(Class<T> clazz, Predicate<T> include) {
        var type = new ConstantArgumentType<>(clazz);

        for (var field : type.clazz.getDeclaredFields()) {
            var modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                var name = field.getName();

                try {
                    //noinspection unchecked
                    if (include.test((T) field.get(null))) {
                        type.validFields.put(name, field);
                    }
                } catch (IllegalAccessException exception) {
                    SoulboundArmory.logger.warn(String.format("Unable to access public static final field %s.", name), exception);
                }
            }
        }

        return type;
    }

    @Override
    public List<T> parse(StringReader reader) throws CommandSyntaxException {
        var input = reader.readString();
        var fields = this.validFields;

        if (Pattern.compile(Pattern.quote("ALL"), Pattern.CASE_INSENSITIVE).matcher(input).find()) {
            //noinspection unchecked
            return this.validFields.values().parallelStream().map(field -> (T) Accessor.get(field)).collect(Collectors.toList());
        }

        for (var name : fields.keySet()) {
            if (Pattern.compile(Pattern.quote(name), Pattern.CASE_INSENSITIVE).matcher(input).find()) {
                try {
                    //noinspection unchecked
                    return Collections.singletonList((T) fields.get(name).get(null));
                } catch (IllegalAccessException ignored) {}
            }
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(Stream.concat(Stream.of("ALL"), this.validFields.values().stream().map(Field::getName)), builder);
    }
}
