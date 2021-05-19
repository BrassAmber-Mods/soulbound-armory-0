package user11681.soulboundarmory.capability.statistics;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.util.Util;

public class StatisticType extends ForgeRegistryEntry<StatisticType> {
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
    public static final StatisticType criticalStrikeProbability = register("critical_strike_probability");
    public static final StatisticType attackRange = register("attack_range");

    public StatisticType(ResourceLocation identifier) {
        this.setRegistryName(identifier);
    }

    private static StatisticType register(String path) {
        StatisticType type = new StatisticType(SoulboundArmory.id(path));
        registry.register(type);

        return type;
    }

    public ResourceLocation id() {
        return registry.getKey(this);
    }
}
