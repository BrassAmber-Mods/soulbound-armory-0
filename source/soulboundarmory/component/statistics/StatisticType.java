package soulboundarmory.component.statistics;

import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.registry.RegistryEntry;
import soulboundarmory.util.Util;

public class StatisticType extends RegistryEntry<StatisticType> {
    @SuppressWarnings("unused")
    public static final IForgeRegistry<StatisticType> registry = Util.newRegistry("statistic");

    public static final StatisticType attributePoints = new StatisticType(Category.datum, "attribute_points");
    public static final StatisticType enchantmentPoints = new StatisticType(Category.datum, "enchantment_points");
    public static final StatisticType experience = new StatisticType(Category.datum, "xp");
    public static final StatisticType level = new StatisticType(Category.datum, "level");
    public static final StatisticType skillPoints = new StatisticType(Category.datum, "skill_points");

    public static final StatisticType attackSpeed = new StatisticType(Category.attribute, "attack_speed");
    public static final StatisticType attackDamage = new StatisticType(Category.attribute, "attack_damage");
    public static final StatisticType criticalHitRate = new StatisticType(Category.attribute, "critical_hit_rate");
    public static final StatisticType efficiency = new StatisticType(Category.attribute, "efficiency");
    public static final StatisticType reach = new StatisticType(Category.attribute, "reach");
    public static final StatisticType upgradeProgress = new StatisticType(Category.attribute, "upgrade_progress");

    public final Category category;

    public StatisticType(Category category, String path) {
        super(path);

        this.category = category;
    }

    @Override
    public String toString() {
        return "statistic type " + this.getRegistryName();
    }
}
