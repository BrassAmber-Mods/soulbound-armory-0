package soulboundarmory.component.statistics;

import java.util.function.Consumer;
import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.module.transform.Register;
import soulboundarmory.module.transform.RegisterAll;
import soulboundarmory.module.transform.Registry;
import soulboundarmory.registry.Identifiable;

@RegisterAll(type = StatisticType.class, registry = "statistic")
public class StatisticType extends Identifiable {
    @Register("attribute_points") public static final StatisticType attributePoints = new StatisticType(Category.datum);
    @Register("enchantment_points") public static final StatisticType enchantmentPoints = new StatisticType(Category.datum);
    @Register("xp") public static final StatisticType experience = new StatisticType(Category.datum);
    @Register("level") public static final StatisticType level = new StatisticType(Category.datum);
    @Register("skill_points") public static final StatisticType skillPoints = new StatisticType(Category.datum);

    @Register("attack_speed") public static final StatisticType attackSpeed = new StatisticType(Category.attribute, statistic -> statistic.defaultMax(4));
    @Register("attack_damage") public static final StatisticType attackDamage = new StatisticType(Category.attribute);
    @Register("critical_hit_rate") public static final StatisticType criticalHitRate = new StatisticType(Category.attribute, statistic -> statistic.defaultMax(1));
    @Register("efficiency") public static final StatisticType efficiency = new StatisticType(Category.attribute);
    @Register("reach") public static final StatisticType reach = new StatisticType(Category.attribute);
    @Register("upgrade_progress") public static final StatisticType upgradeProgress = new StatisticType(Category.attribute);

    public final Category category;

    private final Consumer<Statistic> initialize;

    public StatisticType(Category category, Consumer<Statistic> initialize) {
        this.category = category;
        this.initialize = initialize;
    }

    public StatisticType(Category category) {
        this(category, statistic -> {});
    }

    @Registry("statistic") public static native IForgeRegistry<StatisticType> registry();

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
