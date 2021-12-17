package soulboundarmory.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

public class RegistryArgumentType<T extends IForgeRegistryEntry<?>> implements ArgumentType<Set<T>> {
    protected final IForgeRegistry<?> registry;

    protected RegistryArgumentType(IForgeRegistry<?> registry) {
        this.registry = registry;
    }

    public static <T extends IForgeRegistryEntry<T>> RegistryArgumentType<T> registry(IForgeRegistry<T> registry) {
        return new RegistryArgumentType<>(registry);
    }

    public static <T extends IForgeRegistryEntry> Set<T> get(CommandContext<?> context, String name) {
        return context.getArgument(name, Set.class);
    }

    @Override
    public Set<T> parse(StringReader reader) throws CommandSyntaxException {
        var input = reader.readString();

        if (Pattern.compile(Pattern.quote("all"), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(input).find()) {
            return new HashSet(this.registry.getValues());
        }

        for (var name : this.registry.getKeys()) {
            if (Pattern.compile(Pattern.quote(name.getPath()), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(input).find()) {
                return new ReferenceOpenHashSet<>((T[]) new IForgeRegistryEntry<?>[]{this.registry.getValue(name)});
            }
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.suggestions(context, builder), builder);
    }

    protected <S> Stream<String> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return Stream.concat(this.registry.getKeys().stream().map(Identifier::getPath), Stream.of("all"));
    }

    public static class Serializer implements ArgumentSerializer<RegistryArgumentType<?>> {
        @Override
        public void toPacket(RegistryArgumentType<?> type, PacketByteBuf buf) {
            buf.writeIdentifier(type.registry.getRegistryName());
        }

        @Override
        public RegistryArgumentType<?> fromPacket(PacketByteBuf buf) {
            return new RegistryArgumentType<>(RegistryManager.ACTIVE.getRegistry(buf.readIdentifier()));
        }

        @Override
        public void toJson(RegistryArgumentType<?> type, JsonObject json) {
            json.addProperty("registry", type.registry.getRegistryName().toString());
        }
    }
}
