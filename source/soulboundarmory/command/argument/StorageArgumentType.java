package soulboundarmory.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.StorageType;

public class StorageArgumentType extends RegistryArgumentType<StorageType<?>> {
    protected StorageArgumentType() {
        super(StorageType.registry());
    }

    public static StorageArgumentType storages() {
        return new StorageArgumentType();
    }

    public static Set<StorageType<? extends ItemStorage<?>>> get(CommandContext<?> context, String name) {
        return context.getArgument(name, Set.class);
    }

    @Override
    public Set<StorageType<? extends ItemStorage<?>>> parse(StringReader reader) throws CommandSyntaxException {
        var cursor = reader.getCursor();

        if (Pattern.compile(Pattern.quote("current"), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(reader.readString()).find()) {
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
