package user11681.soulboundarmory.command;

import com.mojang.brigadier.context.CommandContext;
import java.util.Set;
import user11681.soulboundarmory.capability.statistics.StatisticType;

public class StatisticArgumentType extends RegistryArgumentType<StatisticType>  {
    protected StatisticArgumentType() {
        super(StatisticType.registry);
    }

    public static StatisticArgumentType statisticTypes() {
        return new StatisticArgumentType();
    }

    @SuppressWarnings("unchecked")
    public static Set<StatisticType> getTypes(CommandContext<?> context, final String name) {
        return (Set<StatisticType>) context.getArgument(name, Set.class);
    }
}
