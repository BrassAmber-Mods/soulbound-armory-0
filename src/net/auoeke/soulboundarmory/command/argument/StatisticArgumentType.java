package net.auoeke.soulboundarmory.command.argument;

import com.mojang.brigadier.context.CommandContext;
import java.util.Set;
import net.auoeke.soulboundarmory.capability.statistics.StatisticType;

public class StatisticArgumentType extends RegistryArgumentType<StatisticType> {
    protected StatisticArgumentType() {
        super(StatisticType.registry);
    }

    public static StatisticArgumentType statisticTypes() {
        return new StatisticArgumentType();
    }

    @SuppressWarnings("unchecked")
    public static Set<StatisticType> get(CommandContext<?> context, String name) {
        return (Set<StatisticType>) context.getArgument(name, Set.class);
    }
}
