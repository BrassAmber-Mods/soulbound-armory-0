package soulboundarmory.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.util.Util;

public class StorageArgumentType extends RegistryArgumentType<ItemComponentType<?>> {
    protected StorageArgumentType() {
        super(ItemComponentType.registry);
    }

    public static StorageArgumentType storages() {
        return new StorageArgumentType();
    }

    public static Set<ItemComponentType<? extends ItemComponent<?>>> get(CommandContext<?> context, String name) {
        return context.getArgument(name, Set.class);
    }

    @Override
    public Set<ItemComponentType<? extends ItemComponent<?>>> parse(StringReader reader) throws CommandSyntaxException {
        var cursor = reader.getCursor();

        if (Util.containsIgnoreCase(reader.readString(), "current")) {
            return Collections.emptySet();
        }

        reader.setCursor(cursor);

        return super.parse(reader);
    }

    @Override
    protected <S> Stream<String> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return Stream.concat(Stream.of("current"), super.suggestions(context,builder));
    }
}
