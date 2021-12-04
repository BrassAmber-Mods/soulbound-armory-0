package soulboundarmory.component.statistics;

import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.registry.RegistryEntry;
import soulboundarmory.util.Util;

public class StatisticType extends RegistryEntry<StatisticType> {
    @SuppressWarnings("unused")
    public static final IForgeRegistry<StatisticType> registry = Util.registry("statistic");

    public static final StatisticType experience = register("xp");
    public static final StatisticType level = register("level");
    public static final StatisticType skillPoints = register("skill_points");
    public static final StatisticType attributePoints = register("attribute_points");
    public static final StatisticType enchantmentPoints = register("enchantment_points");
    public static final StatisticType spentAttributePoints = register("spent_attribute_points");
    public static final StatisticType spentEnchantmentPoints = register("spent_enchantment_points");
    public static final StatisticType efficiency = register("efficiency");
    public static final StatisticType reach = register("reach");
    public static final StatisticType miningLevel = register("harvest_level");
    public static final StatisticType attackSpeed = register("attack_speed");
    public static final StatisticType attackDamage = register("attack_damage");
    public static final StatisticType criticalStrikeRate = register("critical_strike_rate");
    public static final StatisticType attackRange = register("attack_range");

    @Override
    public String toString() {
        return "statistic type " + this.getRegistryName();
    }
}
