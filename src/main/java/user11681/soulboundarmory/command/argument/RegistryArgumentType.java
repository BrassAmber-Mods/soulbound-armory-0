package user11681.soulboundarmory.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistryArgumentType<T extends IForgeRegistryEntry<?>> implements ArgumentType<Set<T>> {
    protected final IForgeRegistry<?> registry;

    protected RegistryArgumentType(IForgeRegistry<?> registry) {
        this.registry = registry;
    }

    public static <T extends IForgeRegistryEntry<T>> RegistryArgumentType<T> registry(IForgeRegistry<T> registry) {
        return new RegistryArgumentType<>(registry);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Set<T> parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.readString();

        if (Pattern.compile(Pattern.quote("all"), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(input).find()) {
            return new HashSet(this.registry.getValues());
        }

        for (ResourceLocation name : this.registry.getKeys()) {
            if (Pattern.compile(Pattern.quote(name.getPath()), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(input).find()) {
                return (Set<T>) Collections.singleton(this.registry.getValue(name));
            }
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(this.suggestions(context, builder), builder);
    }

    protected <S> Stream<String> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return this.registry.getKeys().stream().map(ResourceLocation::getPath);
    }
}
