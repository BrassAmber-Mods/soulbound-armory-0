package soulboundarmory.command.argument;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import soulboundarmory.util.Util;

public class RegistryArgumentType<T> implements ArgumentType<Set<T>> {
    protected final Registry<T> registry;

    protected RegistryArgumentType(Registry<T> registry) {
        this.registry = registry;
    }

    public static <T> RegistryArgumentType<T> registry(Registry<T> registry) {
        return new RegistryArgumentType<>(registry);
    }

    public static <T> Set<T> get(CommandContext<?> context, String name) {
        return context.getArgument(name, Set.class);
    }

    @Override
    public Set<T> parse(StringReader reader) throws CommandSyntaxException {
        var input = reader.readString();

        if (input.equalsIgnoreCase("all")) {
            return this.registry.stream().collect(Collectors.toSet());
        }

        for (var name : this.registry.getIds()) {
            if (input.equalsIgnoreCase(name.toString()) || input.equalsIgnoreCase(name.getPath())) {
                return ReferenceSet.of(this.registry.get(name));
            }
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, input);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(this.suggestions(context, builder), builder);
    }

    protected <S> Stream<String> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return Stream.concat(Stream.of("all"), this.registry.getIds().stream().map(Identifier::getPath));
    }

    public static class Serializer implements ArgumentSerializer<RegistryArgumentType<?>> {
        @Override
        public void toPacket(RegistryArgumentType<?> type, PacketByteBuf buf) {
            buf.writeIdentifier(Registry.REGISTRIES.getId(Util.cast(type.registry)));
        }

        @Override
        public RegistryArgumentType<?> fromPacket(PacketByteBuf buf) {
            return new RegistryArgumentType<>(Registry.REGISTRIES.get(buf.readIdentifier()));
        }

        @Override
        public void toJson(RegistryArgumentType<?> type, JsonObject json) {
            json.addProperty("registry", Registry.REGISTRIES.getId(Util.cast(type.registry)).toString());
        }
    }
}
