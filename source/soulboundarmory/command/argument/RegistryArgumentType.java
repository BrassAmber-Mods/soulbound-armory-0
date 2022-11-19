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
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public class RegistryArgumentType<T> implements ArgumentType<Set<T>> {
	protected final IForgeRegistry<T> registry;

	protected RegistryArgumentType(IForgeRegistry<T> registry) {
		this.registry = registry;
	}

	public static <T> RegistryArgumentType<T> registry(IForgeRegistry<T> registry) {
		return new RegistryArgumentType<>(registry);
	}

	public static <T> Set<T> get(CommandContext<?> context, String name) {
		return context.getArgument(name, Set.class);
	}

	@Override public Set<T> parse(StringReader reader) {
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

	@Override public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(this.suggestions(context, builder), builder);
	}

	protected <S> Stream<String> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return Stream.concat(Stream.of("all"), this.registry.getKeys().stream().map(Identifier::getPath));
	}

	public static class Serializer<T> implements ArgumentSerializer<RegistryArgumentType<T>, Serializer.Properties<T>> {
		@Override public void writePacket(Serializer.Properties properties, PacketByteBuf buf) {
			buf.writeIdentifier(properties.registry.getRegistryName());
		}

		@Override public Properties<T> fromPacket(PacketByteBuf buf) {
			return new Properties<>(this, RegistryManager.ACTIVE.getRegistry(buf.readIdentifier()));
		}

		@Override public void writeJson(Serializer.Properties properties, JsonObject json) {
			json.addProperty("registry", properties.registry.getRegistryName().toString());
		}

		@Override public Properties<T> getArgumentTypeProperties(RegistryArgumentType<T> argumentType) {
			return new Properties<>(this, argumentType.registry);
		}

		private record Properties<T>(Serializer serializer, IForgeRegistry<T> registry) implements ArgumentTypeProperties<RegistryArgumentType<T>> {
			@Override public RegistryArgumentType<T> createType(CommandRegistryAccess commandRegistryAccess) {
				return RegistryArgumentType.registry(this.registry);
			}

			@Override public ArgumentSerializer<RegistryArgumentType<T>, ?> getSerializer() {
				return this.serializer;
			}
		}
	}
}
