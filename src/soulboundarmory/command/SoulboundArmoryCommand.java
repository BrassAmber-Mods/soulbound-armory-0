package soulboundarmory.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.command.argument.ConstantArgumentType;
import soulboundarmory.command.argument.StatisticArgumentType;
import soulboundarmory.command.argument.StorageArgumentType;
import soulboundarmory.text.Translation;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.event.RegisterCommandsEvent;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static soulboundarmory.command.argument.RegistryArgumentType.registry;
import static soulboundarmory.command.argument.StatisticArgumentType.statisticTypes;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SoulboundArmoryCommand {
    protected static final DynamicCommandExceptionType noItemException = new DynamicCommandExceptionType(player -> new Translation(Translations.commandNoItem.getKey(), ((PlayerEntity) player).getName()));

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            literal("sa").requires(source -> source.hasPermissionLevel(2))
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
                        .then(argument("players", EntityArgumentType.players()).executes(context -> reset(context, true, false)))))
        );
    }

    protected static int add(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
        var players = players(context, hasPlayerArgument);
        var types = StorageArgumentType.get(context, "storage");

        for (var player : players) {
            for (var statistic : StatisticArgumentType.get(context, "statistic")) {
                if (types.isEmpty()) {
                    storage(player).incrementStatistic(statistic, DoubleArgumentType.getDouble(context, "value"));
                } else for (var type : types) {
                    type.get(player).incrementStatistic(statistic, DoubleArgumentType.getDouble(context, "value"));
                }
            }
        }

        return players.size();
    }

    protected static int set(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
        var players = players(context, hasPlayerArgument);
        var types = StorageArgumentType.get(context, "storage");

        for (ServerPlayerEntity player : players) {
            for (var statistic : StatisticArgumentType.get(context, "statistic")) {
                if (types.isEmpty()) {
                    storage(player).set(statistic, DoubleArgumentType.getDouble(context, "value"));
                } else for (var type : types) {
                    type.get(player).set(statistic, DoubleArgumentType.getDouble(context, "value"));
                }
            }
        }

        return players.size();
    }

    protected static int reset(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument, boolean hasCategoryArgument) throws CommandSyntaxException {
        var players = players(context, hasPlayerArgument);
        var types = StorageArgumentType.get(context, "storage");

        for (var player : players) {
            if (types.isEmpty()) {
                types.add(storage(player).type());
            }

            for (var type : types) {
                if (hasCategoryArgument) {
                    for (var category : ConstantArgumentType.getConstants(context, "statistic", Category.class)) {
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
        return Components.soulbound(player).map(SoulboundComponent::heldItemStorage).filter(Objects::nonNull).findAny().orElseThrow(() -> noItemException.create(player));
    }
}
