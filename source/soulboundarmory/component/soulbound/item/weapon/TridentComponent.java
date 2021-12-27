package soulboundarmory.component.soulbound.item.weapon;

import java.util.stream.Stream;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;

public class TridentComponent extends WeaponComponent<TridentComponent> {
    public TridentComponent(SoulboundComponent<?> component) {
        super(component);

        this.statistics
            .statistics(StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
            .statistics(StatisticType.efficiency)
            .constant(3, StatisticType.reach)
            .min(1.1, StatisticType.attackSpeed)
            .min(5, StatisticType.attackDamage);

        this.enchantments.initialize(enchantment -> Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains));
        this.skills.add(Skills.circumspection, Skills.precision, Skills.nourishment);
    }

    @Override
    public ItemComponentType<TridentComponent> type() {
        return ItemComponentType.trident;
    }

    @Override
    public Item item() {
        return SoulboundItems.trident;
    }

    @Override
    public Item consumableItem() {
        return Items.TRIDENT;
    }

    @Override
    public Text name() {
        return Translations.guiTrident;
    }

    @Override
    public double increase(StatisticType type) {
        if (type == StatisticType.attackDamage) return 0.15;
        if (type == StatisticType.attackSpeed) return 0.03;
        if (type == StatisticType.criticalHitRate) return 0.005;

        return 0;
    }
}
