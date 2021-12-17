package soulboundarmory.component.soulbound.item.weapon;

import java.util.stream.Stream;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;

public class BigswordComponent extends WeaponComponent<BigswordComponent> {
    public BigswordComponent(SoulboundComponent<?> component) {
        super(component);

        this.statistics
            .statistics(StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
            .statistics(StatisticType.efficiency)
            .min(4, StatisticType.attackDamage)
            .min(1, StatisticType.attackSpeed)
            .min(3, StatisticType.reach)
            .max(1, StatisticType.criticalHitRate)
            .max(4, StatisticType.attackSpeed);

        this.enchantments.initialize(enchantment -> Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains));
        this.skills.add(Skills.circumspection, Skills.enderPull, Skills.precision, Skills.nourishment);
    }

    @Override
    public ItemComponentType<BigswordComponent> type() {
        return ItemComponentType.bigsword;
    }

    @Override
    public Item item() {
        return SoulboundItems.bigsword;
    }

    @Override
    public Text name() {
        return Translations.guiBigsword;
    }

    @Override
    public double increase(StatisticType type) {
        if (type == StatisticType.attackDamage) return 0.12;
        if (type == StatisticType.attackSpeed) return 0.025;
        if (type == StatisticType.criticalHitRate) return 0.008;
        if (type == StatisticType.efficiency) return 0.03;

        return 0;
    }
}
