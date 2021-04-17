package user11681.soulboundarmory.command;

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
import net.minecraft.server.command.CommandSource;
import user11681.mirror.reflect.Fields;
import user11681.soulboundarmory.SoulboundArmory;

public class ConstantArgumentType<T> implements ArgumentType<List<T>> {
    protected final Class<T> clazz;
    protected final Map<String, Field> validFields;

    protected ConstantArgumentType(final Class<T> clazz) {
        this.clazz = clazz;
        this.validFields = new LinkedHashMap<>();
    }

    public static <T> List<T> getConstants(final CommandContext<?> context, final String name, final Class<T> clazz) {
        //noinspection unchecked
        return (List<T>) context.getArgument(name, List.class);
    }

    public static <T> ConstantArgumentType<T> allConstants(final Class<T> clazz) {
        final ConstantArgumentType<T> type = new ConstantArgumentType<>(clazz);
        final Map<String, Field> fields = type.validFields;

        for (final Field field : type.clazz.getDeclaredFields()) {
            final int modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                fields.put(field.getName(), field);
            }
        }

        return type;
    }

    public static <T, U> ConstantArgumentType<T> allConstants(final Class<T> holderClass, final Class<U> fieldClass) {
        final ConstantArgumentType<T> type = new ConstantArgumentType<>(holderClass);
        final Map<String, Field> fields = type.validFields;

        for (final Field field : type.clazz.getDeclaredFields()) {
            final int modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && fieldClass.isInstance(Fields.getFieldValue(field))) {
                fields.put(field.getName(), field);
            }
        }

        return type;
    }

    @SafeVarargs
    public static <T> ConstantArgumentType<T> excludeConstants(final Class<T> clazz, final T... values) {
        return excludeConstants(clazz, Arrays.asList(values));
    }

    @SafeVarargs
    public static <T> ConstantArgumentType<T> includeConstants(final Class<T> clazz, final T... values) {
        return includeConstants(clazz, Arrays.asList(values));
    }

    public static <T> ConstantArgumentType<T> excludeConstants(final Class<T> clazz, final List<T> values) {
        return excludeConstants(clazz, values::contains);
    }

    public static <T> ConstantArgumentType<T> includeConstants(final Class<T> clazz, final List<T> values) {
        return includeConstants(clazz, values::contains);
    }

    public static <T> ConstantArgumentType<T> excludeConstants(final Class<T> clazz, final Predicate<T> exclude) {
        return includeConstants(clazz, exclude.negate());
    }

    public static <T> ConstantArgumentType<T> includeConstants(final Class<T> clazz, final Predicate<T> include) {
        final ConstantArgumentType<T> type = new ConstantArgumentType<>(clazz);
        final Map<String, Field> fields = type.validFields;

        for (final Field field : type.clazz.getDeclaredFields()) {
            final int modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                final String name = field.getName();

                try {
                    //noinspection unchecked
                    if (include.test((T) field.get(null))) {
                        fields.put(name, field);
                    }
                } catch (final IllegalAccessException exception) {
                    SoulboundArmory.logger.warn(String.format("Unable to access public static final field %s.", name), exception);
                }
            }
        }

        return type;
    }

    @Override
    public List<T> parse(final StringReader reader) throws CommandSyntaxException {
        final String input = reader.readString();
        final Map<String, Field> fields = this.validFields;

        if (Pattern.compile(Pattern.quote("ALL"), Pattern.CASE_INSENSITIVE).matcher(input).find()) {
            //noinspection unchecked
            return this.validFields.values().parallelStream().map((final Field field) -> (T) Fields.getFieldValue(field)).collect(Collectors.toList());
        }

        for (final String name : fields.keySet()) {
            if (Pattern.compile(Pattern.quote(name), Pattern.CASE_INSENSITIVE).matcher(input).find()) {
                try {
                    //noinspection unchecked
                    return Collections.singletonList((T) fields.get(name).get(null));
                } catch (final IllegalAccessException ignored) {
                }
            }
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final List<String> suggestions = this.validFields.values().parallelStream().map(Field::getName).collect(Collectors.toList());

        suggestions.add("ALL");

        return CommandSource.suggestMatching(suggestions, builder);
    }
}
