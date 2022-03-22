package soulboundarmory.command;

import java.util.Collection;
import java.util.List;
import java.util.Set;
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
    private static final String ITEM = "item";
    private static final String STATISTIC = "statistic";
    private static final String VALUE = "value";

    private static final DynamicCommandExceptionType noItemException = new DynamicCommandExceptionType(player -> Translations.commandNoItem.format(((PlayerEntity) player).getName()));

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            literal("sa").requires(source -> source.hasPermissionLevel(2))
                .then(literal("add")
                    .then(argument(ITEM, itemComponents())
                        .then(argument(STATISTIC, statisticTypes())
                            .then(argument(VALUE, doubleArg()).executes(context -> add(context, false))
                                .then(argument("players", EntityArgumentType.players()).executes(context -> add(context, true)))))))
                .then(literal("get")
                    .then(argument(ITEM, itemComponents())
                        .then(argument(STATISTIC, statisticTypes()).executes(context -> get(context, false))
                            .then(argument("players", EntityArgumentType.players()).executes(context -> get(context, true))))))
                .then(literal("set")
                    .then(argument(ITEM, itemComponents())
                        .then(argument(STATISTIC, statisticTypes())
                            .then(argument(VALUE, doubleArg()).executes(context -> set(context, false))
                                .then(argument("players", EntityArgumentType.players()).executes(context -> set(context, true)))))))
                .then(literal("reset").executes(context -> reset(context, false, false, false))
                    .then(argument(ITEM, itemComponents()).executes(context -> reset(context, true, false, false))
                        .then(argument("category", registry(Category.registry())).executes(context -> reset(context, true, false, true))
                            .then(argument("players", EntityArgumentType.players()).executes(context -> reset(context, true, true, true))))
                        .then(argument("players", EntityArgumentType.players()).executes(context -> reset(context, true, true, false)))))
        );
    }

    private static RegistryArgumentType<StatisticType> statisticTypes() {
        return registry(StatisticType.registry());
    }

    private static int add(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
        var players = players(context, hasPlayerArgument);
        var types = componentTypes(context);

        for (var player : players) {
            for (var statistic : statistics(context)) {
                components(player, types).forEach(item -> item.add(statistic, DoubleArgumentType.getDouble(context, VALUE)));
            }
        }

        return players.size();
    }

    private static int get(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
        var players = players(context, hasPlayerArgument);
        var types = componentTypes(context);

        for (var player : players) {
            for (var statistic : statistics(context)) {
                components(player, types).forEach(item -> context.getSource().sendFeedback(item.format(statistic), false));
            }
        }

        return players.size();
    }

    private static int set(CommandContext<ServerCommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
        var players = players(context, hasPlayerArgument);
        var types = componentTypes(context);

        for (var player : players) {
            for (var statistic : statistics(context)) {
                components(player, types).forEach(item -> item.set(statistic, DoubleArgumentType.getDouble(context, VALUE)));
            }
        }

        return players.size();
    }

    private static int reset(CommandContext<ServerCommandSource> context, boolean hasItemArgument, boolean hasPlayerArgument, boolean hasCategoryArgument) throws CommandSyntaxException {
        var players = players(context, hasPlayerArgument);

        if (hasItemArgument) {
            var types = componentTypes(context);

            for (var player : players) {
                components(player, types).forEach(item -> {
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
            players.forEach(player -> ItemComponent.fromHands(player).ifPresent(ItemComponent::reset));
        }

        return players.size();
    }

    private static Set<ItemComponentType<?>> componentTypes(CommandContext<ServerCommandSource> context) {
        return RegistryArgumentType.get(context, ITEM);
    }

    private static Set<StatisticType> statistics(CommandContext<ServerCommandSource> context) {
        return RegistryArgumentType.get(context, STATISTIC);
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

    private static Stream<ItemComponent<?>> components(PlayerEntity player, Collection<ItemComponentType<?>> types) throws CommandSyntaxException {
        return types.isEmpty() ? Stream.of(item(player)) : types.stream().map(type -> type.of(player));
    }
}
