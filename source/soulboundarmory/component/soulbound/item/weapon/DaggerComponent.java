package soulboundarmory.component.soulbound.item.weapon;

import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.Statistic;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.util.Util;

public class DaggerComponent extends WeaponComponent<DaggerComponent> {
    public DaggerComponent(SoulboundComponent<?> component) {
        super(component);

        this.statistics
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
            .category(Category.attribute, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.criticalHitRate, StatisticType.efficiency, StatisticType.reach)
            .min(2, StatisticType.attackSpeed, StatisticType.attackDamage, StatisticType.reach)
            .max(1, StatisticType.criticalHitRate)
            .max(4, StatisticType.attackSpeed);

        this.enchantments.initialize(enchantment -> Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains));
        this.skills.add(Skills.circumspection, Skills.enderPull, Skills.precision, Skills.nourishment, Skills.throwing, Skills.shadowClone, Skills.returning, Skills.sneakReturn);
    }

    @Override
    public ItemComponentType<DaggerComponent> type() {
        return ItemComponentType.dagger;
    }

    @Override
    public Item item() {
        return SoulboundItems.dagger;
    }

    @Override
    public Text name() {
        return Translations.guiDagger;
    }

    @Override
    public Map<Statistic, Text> screenAttributes() {
        return Util.add(super.screenAttributes(), this.statisticEntry(StatisticType.efficiency, Translations.guiEfficiency));
    }

    @Override
    public double increase(StatisticType type) {
        if (type == StatisticType.attackSpeed) return 0.04;
        if (type == StatisticType.attackDamage) return 0.05;
        if (type == StatisticType.criticalHitRate) return 0.02;
        if (type == StatisticType.efficiency) return 0.06;

        return 0;
    }
}
