package user11681.soulboundarmory.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.capability.statistics.Category;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.command.argument.ConstantArgumentType;
import user11681.soulboundarmory.command.argument.StatisticArgumentType;
import user11681.soulboundarmory.command.argument.StorageArgumentType;
import user11681.soulboundarmory.text.Translation;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;
import static user11681.soulboundarmory.command.argument.RegistryArgumentType.registry;
import static user11681.soulboundarmory.command.argument.StatisticArgumentType.statisticTypes;
import static user11681.soulboundarmory.command.argument.StorageArgumentType.storages;

public class SoulboundArmoryCommand {
    protected static final DynamicCommandExceptionType noItemException = new DynamicCommandExceptionType((Object player) -> new Translation(Translations.commandNoItem.getKey(), ((PlayerEntity) player).getName()));

    public static LiteralArgumentBuilder<CommandSource> get() {
        return (literal("sa").requires((CommandSource source) -> source.hasPermission(2)))
                .then(literal("add")
                        .then(argument("storage", storages())
                                .then(argument("statistic", statisticTypes())
                                        .then(argument("value", doubleArg()).executes((CommandContext<CommandSource> context) -> add(context, false))
                                                .then(argument("players", EntityArgument.players()).executes((CommandContext<CommandSource> context) -> add(context, true)))))))
                .then(literal("set")
                        .then(argument("storage", storages())
                                .then(argument("statistic", statisticTypes())
                                        .then(argument("value", doubleArg()).executes((CommandContext<CommandSource> context) -> set(context, false))
                                                .then(argument("players", EntityArgument.players()).executes((CommandContext<CommandSource> context) -> set(context, true)))))))
                .then(literal("reset")
                        .then(argument("storage", storages()).executes((CommandContext<CommandSource> context) -> reset(context, false, false))
                                .then(argument("category", registry(Category.registry)).executes((CommandContext<CommandSource> context) -> reset(context, false, true))
                                        .then(argument("players", EntityArgument.players()).executes((CommandContext<CommandSource> context) -> reset(context, true, true))))
                                .then(argument("players", EntityArgument.players()).executes((CommandContext<CommandSource> context) -> reset(context, true, false)))));
    }

    protected static int add(CommandContext<CommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
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

    protected static int set(CommandContext<CommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
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

    protected static int reset(CommandContext<CommandSource> context, boolean hasPlayerArgument, boolean hasCategoryArgument) throws CommandSyntaxException {
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

    protected static Collection<ServerPlayerEntity> players(CommandContext<CommandSource> context, boolean hasPlayerArgument) throws CommandSyntaxException {
        return hasPlayerArgument ? EntityArgument.getPlayers(context, "players") : Collections.singleton(player(context));
    }

    protected static ServerPlayerEntity player(CommandContext<CommandSource> context) {
        return (ServerPlayerEntity) context.getSource().getEntity();
    }

    protected static ItemStorage<?> storage(PlayerEntity player) throws CommandSyntaxException {
        return Capabilities.get(player).map(SoulboundCapability::heldItemStorage).filter(Objects::nonNull).findAny().orElseThrow(() -> noItemException.create(player));
    }
}
