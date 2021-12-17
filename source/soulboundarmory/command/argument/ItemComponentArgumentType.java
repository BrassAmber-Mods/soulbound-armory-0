package soulboundarmory.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.registries.RegistryManager;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.util.Util;

public class ItemComponentArgumentType extends RegistryArgumentType<ItemComponentType<?>> {
    protected ItemComponentArgumentType() {
        super(ItemComponentType.registry);
    }

    public static ItemComponentArgumentType itemComponents() {
        return new ItemComponentArgumentType();
    }

    @Override
    public Set<ItemComponentType<? extends ItemComponent<?>>> parse(StringReader reader) throws CommandSyntaxException {
        var cursor = reader.getCursor();

        if (Util.containsIgnoreCase(reader.readString(), "current")) {
            return new ReferenceOpenHashSet<>();
        }

        reader.setCursor(cursor);

        return super.parse(reader);
    }

    @Override
    protected <S> Stream<String> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return Stream.concat(Stream.of("current"), super.suggestions(context,builder));
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
