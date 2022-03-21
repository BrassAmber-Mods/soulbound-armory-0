package soulboundarmory.component.statistics;

import java.util.function.Consumer;
import net.minecraft.util.registry.Registry;
import soulboundarmory.registry.RegistryElement;
import soulboundarmory.util.Util;

public class StatisticType extends RegistryElement<StatisticType> {
    public static final Registry<StatisticType> registry = Util.newRegistry("statistic");

    public static final StatisticType attributePoints = new StatisticType(Category.datum, "attribute_points");
    public static final StatisticType enchantmentPoints = new StatisticType(Category.datum, "enchantment_points");
    public static final StatisticType experience = new StatisticType(Category.datum, "xp");
    public static final StatisticType level = new StatisticType(Category.datum, "level");
    public static final StatisticType skillPoints = new StatisticType(Category.datum, "skill_points");

    public static final StatisticType attackSpeed = new StatisticType(Category.attribute, "attack_speed", statistic -> statistic.defaultMax(4));
    public static final StatisticType attackDamage = new StatisticType(Category.attribute, "attack_damage");
    public static final StatisticType criticalHitRate = new StatisticType(Category.attribute, "critical_hit_rate", statistic -> statistic.defaultMax(1));
    public static final StatisticType efficiency = new StatisticType(Category.attribute, "efficiency");
    public static final StatisticType reach = new StatisticType(Category.attribute, "reach");
    public static final StatisticType upgradeProgress = new StatisticType(Category.attribute, "upgrade_progress");

    public final Category category;

    private final Consumer<Statistic> initialize;

    public StatisticType(Category category, String path, Consumer<Statistic> initialize) {
        super(path);

        this.category = category;
        this.initialize = initialize;
    }

    public StatisticType(Category category, String path) {
        this(category, path, statistic -> {});
    }

    public final Statistic instantiate() {
        var statistic = new Statistic(this);
        this.initialize.accept(statistic);

        return statistic;
    }

    @Override
    public String toString() {
        return "statistic type " + this.id();
    }
}
