package net.auoeke.soulboundarmory.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import net.auoeke.soulboundarmory.capability.statistics.StatisticType;
import net.auoeke.soulboundarmory.command.argument.StorageArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.auoeke.soulboundarmory.capability.Capabilities;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.StorageType;
import net.auoeke.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import net.auoeke.soulboundarmory.capability.statistics.Category;
import net.auoeke.soulboundarmory.client.i18n.Translations;
import net.auoeke.soulboundarmory.command.argument.ConstantArgumentType;
import net.auoeke.soulboundarmory.command.argument.StatisticArgumentType;
import net.auoeke.soulboundarmory.text.Translation;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.auoeke.soulboundarmory.command.argument.RegistryArgumentType.registry;
import static net.auoeke.soulboundarmory.command.argument.StatisticArgumentType.statisticTypes;

public class SoulboundArmoryCommand {
    protected static final DynamicCommandExceptionType noItemException = new DynamicCommandExceptionType((Object player) -> new Translation(Translations.commandNoItem.getKey(), ((PlayerEntity) player).getName()));

    public static LiteralArgumentBuilder<ServerCommandSource> get() {
        return literal("sa").requires(source -> source.hasPermissionLevel(2))
            .then(literal("add")
                .then(argument("storage", StorageArgumentType.storages())
                    .then(argument("statistic", statisticTypes())
                        .then(argument("value", doubleArg()).executes(context -> add(context, false))
                            .then(argument("players", EntityArgumentType.players()).executes(context -> add(context, true)))))))
            .then(literal("set")
                .then(argument("storage", StorageArgumentType.storages())
                    .then(argument("statistic", statisticTypes())
                        .then(argument("value", doubleArg()).executes(context -> set(context, false))
                            .then(argument("players", EntityArgumentType.players()).executes(context -> set(context, true)))))))
            .then(literal("reset")
                .then(argument("storage", StorageArgumentType.storages()).executes(context -> reset(context, false, false))
                    .then(argument("category", registry(Category.registry)).executes(context -> reset(context, false, true))
                        .then(argument("players", EntityArgumentType.players()).executes(context -> reset(context, true, true))))
                    .then(argument("players", EntityArgumentType.players()).executes(context -> reset(context, true, false)))));
    }

    protected static int add(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = players(context, hasPlayerArgument);
        Set<StorageType<? extends ItemStorage<?>>> types = StorageArgumentType.get(context, "storage");

        for (PlayerEntity player : players) {
            for (StatisticType statistic : StatisticArgumentType.get(context, "statistic")) {
                if (types.isEmpty()) {
                    storage(player).incrementStatistic(statistic, DoubleArgumentType.getDouble(context, "value"));
                } else {
                    for (StorageType<? extends ItemStorage<?>> type : types) {
                        type.get(player).incrementStatistic(statistic, DoubleArgumentType.getDouble(context, "value"));
                    }
                }
            }
        }

        return players.size();
    }

    protected static int set(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = players(context, hasPlayerArgument);
        Set<StorageType<? extends ItemStorage<?>>> types = StorageArgumentType.get(context, "storage");

        for (PlayerEntity player : players) {
            for (StatisticType statistic : StatisticArgumentType.get(context, "statistic")) {
                if (types.isEmpty()) {
                    storage(player).set(statistic, DoubleArgumentType.getDouble(context, "value"));
                } else {
                    for (StorageType<? extends ItemStorage<?>> type : types) {
                        type.get(player).set(statistic, DoubleArgumentType.getDouble(context, "value"));
                    }
                }
            }
        }

        return players.size();
    }

    protected static int reset(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument, boolean hasCategoryArgument) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = players(context, hasPlayerArgument);
        Set<StorageType<? extends ItemStorage<?>>> types = StorageArgumentType.get(context, "storage");

        for (PlayerEntity player : players) {
            if (types.isEmpty()) {
                types.add(storage(player).type());
            }

            for (StorageType<?> type : types) {
                if (hasCategoryArgument) {
                    for (Category category : ConstantArgumentType.getConstants(context, "statistic", Category.class)) {
                        type.get(player).reset(category);
                    }
                } else {
                    type.get(player).reset();
                }
            }
        }

        return players.size();
    }

    protected static Collection<ServerPlayerEntity> players(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
        return hasPlayerArgument ? EntityArgumentType.getPlayers(context, "players") : Collections.singleton(player(context));
    }

    protected static ServerPlayerEntity player(CommandContext<ServerCommandSource> context) {
        return (ServerPlayerEntity) context.getSource().getEntity();
    }

    protected static ItemStorage<?> storage(PlayerEntity player) throws CommandSyntaxException {
        return Capabilities.get(player).map(SoulboundCapability::heldItemStorage).filter(Objects::nonNull).findAny().orElseThrow(() -> noItemException.create(player));
    }
}
