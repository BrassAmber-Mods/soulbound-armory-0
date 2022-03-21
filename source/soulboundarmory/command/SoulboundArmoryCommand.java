package soulboundarmory.command;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.command.argument.ItemComponentArgumentType;
import soulboundarmory.command.argument.RegistryArgumentType;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static soulboundarmory.command.argument.ItemComponentArgumentType.itemComponents;
import static soulboundarmory.command.argument.RegistryArgumentType.registry;

public final class SoulboundArmoryCommand {
    private static final DynamicCommandExceptionType noItemException = new DynamicCommandExceptionType(player -> Translations.commandNoItem.format(((PlayerEntity) player).getName()));

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            literal("sa").requires(source -> source.hasPermissionLevel(2))
                .then(literal("add")
                    .then(argument("item", itemComponents())
                        .then(argument("statistic", statisticTypes())
                            .then(argument("value", doubleArg()).executes(context -> add(context, false))
                                .then(argument("players", EntityArgumentType.players()).executes(context -> add(context, true)))))))
                .then(literal("set")
                    .then(argument("item", itemComponents())
                        .then(argument("statistic", statisticTypes())
                            .then(argument("value", doubleArg()).executes(context -> set(context, false))
                                .then(argument("players", EntityArgumentType.players()).executes(context -> set(context, true)))))))
                .then(literal("reset").executes(context -> reset(context, false, false, false))
                    .then(argument("item", itemComponents()).executes(context -> reset(context, true, false, false))
                        .then(argument("category", registry(Category.registry)).executes(context -> reset(context, true, false, true))
                            .then(argument("players", EntityArgumentType.players()).executes(context -> reset(context, true, true, true))))
                        .then(argument("players", EntityArgumentType.players()).executes(context -> reset(context, true, true, false)))))
        );
    }

    private static RegistryArgumentType<StatisticType> statisticTypes() {
        return registry(StatisticType.registry);
    }

    private static int add(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
        var players = players(context, hasPlayerArgument);
        var types = RegistryArgumentType.<ItemComponentType<?>>get(context, "item");

        for (var player : players) {
            for (var statistic : RegistryArgumentType.<StatisticType>get(context, "statistic")) {
                items(player, types).forEach(item -> item.add(statistic, DoubleArgumentType.getDouble(context, "value")));
            }
        }

        return players.size();
    }

    private static int set(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
        var players = players(context, hasPlayerArgument);
        var types = RegistryArgumentType.<ItemComponentType<?>>get(context, "item");

        for (var player : players) {
            for (var statistic : RegistryArgumentType.<StatisticType>get(context, "statistic")) {
                items(player, types).forEach(item -> item.set(statistic, DoubleArgumentType.getDouble(context, "value")));
            }
        }

        return players.size();
    }

    private static int reset(CommandContext<ServerCommandSource> context, boolean hasItemArgument, boolean hasPlayerArgument, boolean hasCategoryArgument) throws CommandSyntaxException {
        var players = players(context, hasPlayerArgument);

        if (hasItemArgument) {
            var types = ItemComponentArgumentType.<ItemComponentType<?>>get(context, "item");

            for (var player : players) {
                items(player, types).forEach(item -> {
                    if (hasCategoryArgument) {
                        for (var category : RegistryArgumentType.<Category>get(context, "category")) {
                            item.reset(category);
                        }
                    } else {
                        item.reset();
                    }
                });
            }
        } else {
            players.forEach(entity -> ItemComponent.fromHands(entity).ifPresent(ItemComponent::reset));
        }

        return players.size();
    }

    private static Collection<ServerPlayerEntity> players(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
        return hasPlayerArgument ? EntityArgumentType.getPlayers(context, "players") : List.of(player(context));
    }

    private static ServerPlayerEntity player(CommandContext<ServerCommandSource> context) {
        return (ServerPlayerEntity) context.getSource().getEntity();
    }

    private static ItemComponent<?> item(PlayerEntity player) throws CommandSyntaxException {
        return ItemComponent.fromHands(player).orElseThrow(() -> noItemException.create(player));
    }

    private static Stream<ItemComponent<?>> items(PlayerEntity player, Collection<ItemComponentType<?>> types) throws CommandSyntaxException {
        return types.isEmpty() ? Stream.of(item(player)) : types.stream().map(type -> type.of(player));
    }
}
