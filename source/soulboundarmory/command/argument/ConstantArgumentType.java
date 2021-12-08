package soulboundarmory.command.argument;

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
import java.util.stream.Stream;
import net.auoeke.reflect.Accessor;
import net.minecraft.command.CommandSource;

public class ConstantArgumentType<T> implements ArgumentType<List<T>> {
    protected final Class<T> type;
    protected final Map<String, Field> validFields;

    protected ConstantArgumentType(Class<T> type) {
        this.type = type;
        this.validFields = new LinkedHashMap<>();
    }

    public static <T> List<T> getConstants(CommandContext<?> context, String name, Class<T> type) {
        //noinspection unchecked
        return (List<T>) context.getArgument(name, List.class);
    }

    public static <T> ConstantArgumentType<T> allConstants(Class<T> type) {
        var argumentType = new ConstantArgumentType<>(type);

        for (var field : type.getDeclaredFields()) {
            var modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                argumentType.validFields.put(field.getName(), field);
            }
        }

        return argumentType;
    }

    public static <T, U> ConstantArgumentType<T> allConstants(Class<T> holderClass, Class<U> fieldClass) {
        var type = new ConstantArgumentType<>(holderClass);

        for (var field : type.type.getDeclaredFields()) {
            var modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && fieldClass.isInstance(Accessor.get(field))) {
                type.validFields.put(field.getName(), field);
            }
        }

        return type;
    }

    @SafeVarargs
    public static <T> ConstantArgumentType<T> excludeConstants(Class<T> type, T... values) {
        return excludeConstants(type, Arrays.asList(values));
    }

    @SafeVarargs
    public static <T> ConstantArgumentType<T> includeConstants(Class<T> type, T... values) {
        return includeConstants(type, Arrays.asList(values));
    }

    public static <T> ConstantArgumentType<T> excludeConstants(Class<T> type, List<T> values) {
        return excludeConstants(type, values::contains);
    }

    public static <T> ConstantArgumentType<T> includeConstants(Class<T> type, List<T> values) {
        return includeConstants(type, values::contains);
    }

    public static <T> ConstantArgumentType<T> excludeConstants(Class<T> type, Predicate<T> exclude) {
        return includeConstants(type, exclude.negate());
    }

    public static <T> ConstantArgumentType<T> includeConstants(Class<T> type, Predicate<T> include) {
        var argumentType = new ConstantArgumentType<>(type);

        for (var field : type.getDeclaredFields()) {
            var modifiers = field.getModifiers();

            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && include.test((T) Accessor.get(field))) {
                argumentType.validFields.put(field.getName(), field);
            }
        }

        return argumentType;
    }

    @Override
    public List<T> parse(StringReader reader) throws CommandSyntaxException {
        var input = reader.readString();
        var fields = this.validFields;

        if (input.equals("*")) {
            //noinspection unchecked
            return this.validFields.values().stream().map(field -> (T) Accessor.get(field)).toList();
        }

        for (var name : fields.keySet()) {
            if (Pattern.compile(Pattern.quote(name), Pattern.CASE_INSENSITIVE).matcher(input).find()) {
                return Collections.singletonList((T) Accessor.getObject(fields.get(name)));
            }
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(Stream.concat(Stream.of("*"), this.validFields.values().stream().map(Field::getName)), builder);
    }
}
