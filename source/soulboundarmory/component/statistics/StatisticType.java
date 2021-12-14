package soulboundarmory.component.statistics;

import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.registry.RegistryEntry;
import soulboundarmory.util.Util;

public class StatisticType extends RegistryEntry<StatisticType> {
    @SuppressWarnings("unused")
    public static final IForgeRegistry<StatisticType> registry = Util.newRegistry("statistic");

    public static final StatisticType experience = new StatisticType("xp");
    public static final StatisticType level = new StatisticType("level");
    public static final StatisticType skillPoints = new StatisticType("skill_points");
    public static final StatisticType attributePoints = new StatisticType("attribute_points");
    public static final StatisticType enchantmentPoints = new StatisticType("enchantment_points");
    public static final StatisticType efficiency = new StatisticType("efficiency");
    public static final StatisticType reach = new StatisticType("reach");
    public static final StatisticType miningLevel = new StatisticType("harvest_level");
    public static final StatisticType attackSpeed = new StatisticType("attack_speed");
    public static final StatisticType attackDamage = new StatisticType("attack_damage");
    public static final StatisticType criticalStrikeRate = new StatisticType("critical_strike_rate");

    public StatisticType(String path) {
        super(path);
    }

    @Override
    public String toString() {
        return "statistic type " + this.getRegistryName();
    }
}
