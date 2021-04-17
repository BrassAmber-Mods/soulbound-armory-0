package user11681.soulboundarmory.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.statistics.Category;
import user11681.soulboundarmory.component.statistics.StatisticType;
import user11681.soulboundarmory.text.StringableText;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static user11681.soulboundarmory.command.RegistryArgumentType.registry;
import static user11681.soulboundarmory.command.StatisticArgumentType.statisticTypes;
import static user11681.soulboundarmory.command.StorageArgumentType.storages;

;

public class SoulboundArmoryCommand {
    protected static final DynamicCommandExceptionType NO_ITEM_EXCEPTION = new DynamicCommandExceptionType((final Object player) -> new StringableText(Translations.commandNoItem.getKey(), ((PlayerEntity) player).getName()));

    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher, final boolean dedicated) {
        dispatcher.register((literal("sba").requires((final ServerCommandSource source) -> source.hasPermissionLevel(2)))
                .then(literal("add")
                        .then(argument("storage", storages())
                                .then(argument("statistic", statisticTypes())
                                        .then(argument("value", doubleArg()).executes((final CommandContext<ServerCommandSource> context) -> add(context, false))
                                                .then(argument("players", players()).executes((final CommandContext<ServerCommandSource> context) -> add(context, true)))))))
                .then(literal("set")
                        .then(argument("storage", storages())
                                .then(argument("statistic", statisticTypes())
                                        .then(argument("value", doubleArg()).executes((final CommandContext<ServerCommandSource> context) -> set(context, false))
                                                .then(argument("players", players()).executes((final CommandContext<ServerCommandSource> context) -> set(context, true)))))))
                .then(literal("reset")
                        .then(argument("storage", storages()).executes((final CommandContext<ServerCommandSource> context) -> reset(context, false, false))
                                .then(argument("category", registry(Category.category)).executes((final CommandContext<ServerCommandSource> context) -> reset(context, false, true))
                                        .then(argument("players", players()).executes((final CommandContext<ServerCommandSource> context) -> reset(context, true, true))))
                                .then(argument("players", players()).executes((final CommandContext<ServerCommandSource> context) -> reset(context, true, false)))))
        );
    }

    protected static int add(final CommandContext<ServerCommandSource> context, final boolean hasPlayerArgument) throws CommandSyntaxException {
        final Collection<ServerPlayerEntity> players = getPlayers(context, hasPlayerArgument);
        final Set<StorageType<? extends ItemStorage<?>>> types = StorageArgumentType.getStorages(context, "storage");

        for (final PlayerEntity player : players) {
            for (final StatisticType statistic : StatisticArgumentType.getTypes(context, "statistic")) {
                if (types.isEmpty()) {
                    getStorage(player).incrementStatistic(statistic, DoubleArgumentType.getDouble(context, "value"));
                } else {
                    for (final StorageType<? extends ItemStorage<?>> type : types) {
                        type.get(player).incrementStatistic(statistic, DoubleArgumentType.getDouble(context, "value"));
                    }
                }
            }
        }

        return players.size();
    }

    protected static int set(final CommandContext<ServerCommandSource> context, final boolean hasPlayerArgument) throws CommandSyntaxException {
        final Collection<ServerPlayerEntity> players = getPlayers(context, hasPlayerArgument);
        final Set<StorageType<? extends ItemStorage<?>>> types = StorageArgumentType.getStorages(context, "storage");

        for (final PlayerEntity player : players) {
            for (final StatisticType statistic : StatisticArgumentType.getTypes(context, "statistic")) {
                if (types.isEmpty()) {
                    getStorage(player).setStatistic(statistic, DoubleArgumentType.getDouble(context, "value"));
                } else {
                    for (final StorageType<? extends ItemStorage<?>> type : types) {
                        type.get(player).setStatistic(statistic, DoubleArgumentType.getDouble(context, "value"));
                    }
                }
            }
        }

        return players.size();
    }

    protected static int reset(final CommandContext<ServerCommandSource> context, final boolean hasPlayerArgument, final boolean hasCategoryArgument) throws CommandSyntaxException {
        final Collection<ServerPlayerEntity> players = getPlayers(context, hasPlayerArgument);
        final Set<StorageType<? extends ItemStorage<?>>> types = StorageArgumentType.getStorages(context, "storage");

        for (final PlayerEntity player : players) {
            if (types.isEmpty()) {
                types.add(getStorage(player).getType());
            }

            for (final StorageType<?> type : types) {
                if (hasCategoryArgument) {
                    for (final Category category : ConstantArgumentType.getConstants(context, "statistic", Category.class)) {
                        type.get(player).reset(category);
                    }
                } else {
                    type.get(player).reset();
                }
            }
        }

        return players.size();
    }

    protected static Collection<ServerPlayerEntity> getPlayers(final CommandContext<ServerCommandSource> context, final boolean hasPlayerArgument) throws CommandSyntaxException {
        final Collection<ServerPlayerEntity> players;

        if (hasPlayerArgument) {
            players = EntityArgumentType.getPlayers(context, "players");
        } else {
            players = Collections.singleton(context.getSource().getPlayer());
        }

        return players;
    }

    protected static ItemStorage<?> getStorage(final PlayerEntity player) throws CommandSyntaxException {
        for (SoulboundComponent<?> component : Components.getComponents(player)) {
            final ItemStorage<?> storage = component.heldItemStorage();

            if (storage != null) {
                return storage;
            }
        }

        throw NO_ITEM_EXCEPTION.create(player);
    }
}
