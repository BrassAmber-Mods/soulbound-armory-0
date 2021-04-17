package user11681.soulboundarmory.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryArgumentType<T> implements ArgumentType<Set<T>> {
    protected final Registry<T> registry;

    protected RegistryArgumentType(Registry<T> registry) {
        this.registry = registry;
    }

    public static <T> RegistryArgumentType<T> registry(Registry<T> registry) {
        return new RegistryArgumentType<>(registry);
    }

    @Override
    public Set<T> parse(final StringReader reader) throws CommandSyntaxException {
        String input = reader.readString();
        Registry<T> entries = this.registry;

        if (Pattern.compile(Pattern.quote("all"), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(input).find()) {
            return entries.stream().collect(Collectors.toSet());
        }

        for (Identifier name : this.registry.getIds()) {
            if (Pattern.compile(Pattern.quote(name.getPath()), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(input).find()) {
                return Collections.singleton(entries.get(name));
            }
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.getSuggestions(context, builder), builder);
    }

    protected <S> Collection<String> getSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return this.registry.getIds().parallelStream().map(Identifier::getPath).collect(Collectors.toSet());
    }
}
