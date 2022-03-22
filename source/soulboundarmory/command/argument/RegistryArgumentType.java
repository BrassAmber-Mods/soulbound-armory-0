package soulboundarmory.command.argument;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

public class RegistryArgumentType<T extends IForgeRegistryEntry<T>> implements ArgumentType<Set<T>> {
    protected final IForgeRegistry<T> registry;

    protected RegistryArgumentType(IForgeRegistry<T> registry) {
        this.registry = registry;
    }

    public static <T extends IForgeRegistryEntry<T>> RegistryArgumentType<T> registry(IForgeRegistry<T> registry) {
        return new RegistryArgumentType<>(registry);
    }

    public static <T> Set<T> get(CommandContext<?> context, String name) {
        return context.getArgument(name, Set.class);
    }

    @Override
    public Set<T> parse(StringReader reader) throws CommandSyntaxException {
        var input = reader.readString();

        if (input.equalsIgnoreCase("all")) {
            return new ReferenceOpenHashSet<>(this.registry.getValues());
        }

        for (var name : this.registry.getKeys()) {
            if (input.equalsIgnoreCase(name.toString()) || input.equalsIgnoreCase(name.getPath())) {
                return ReferenceSet.of(this.registry.getValue(name));
            }
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.suggestions(context, builder), builder);
    }

    protected <S> Stream<String> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return Stream.concat(Stream.of("all"), this.registry.getKeys().stream().map(Identifier::getPath));
    }

    public static class Serializer<T extends IForgeRegistryEntry<T>> implements ArgumentSerializer<RegistryArgumentType<T>> {
        @Override
        public void toPacket(RegistryArgumentType<T> type, PacketByteBuf buf) {
            buf.writeIdentifier(type.registry.getRegistryName());
        }

        @Override
        public RegistryArgumentType<T> fromPacket(PacketByteBuf buf) {
            return new RegistryArgumentType<>(RegistryManager.ACTIVE.<T>getRegistry(buf.readIdentifier()));
        }

        @Override
        public void toJson(RegistryArgumentType<T> type, JsonObject json) {
            json.addProperty("registry", type.registry.getRegistryName().toString());
        }
    }
}
