package soulboundarmory.component.soulbound.item.tool;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import soulboundarmory.client.gui.screen.StatisticEntry;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;

import static net.minecraft.enchantment.Enchantments.MENDING;
import static net.minecraft.enchantment.Enchantments.UNBREAKING;

public class PickComponent extends ToolComponent<PickComponent> {
    public PickComponent(SoulboundComponent component) {
        super(component);

        this.statistics
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints, StatisticType.spentAttributePoints, StatisticType.spentEnchantmentPoints)
            .category(Category.attribute, StatisticType.efficiency, StatisticType.reach, StatisticType.miningLevel)
            .min(1, StatisticType.efficiency).min(2, StatisticType.reach)
            .max(3, StatisticType.miningLevel);

        this.skills.add(Skills.enderPull/*, Skills.ambidexterity*/);

        this.enchantments.add(enchantment -> enchantment.type.isAcceptableItem(this.item())
            && !enchantment.isCursed()
            && !Arrays.asList(UNBREAKING, MENDING).contains(enchantment)
            && Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains)
        );
    }

    @Override
    public ItemComponentType<PickComponent> type() {
        return ItemComponentType.pick;
    }

    @Override
    public Item item() {
        return SoulboundItems.pick;
    }

    @Override
    public Item consumableItem() {
        return Items.WOODEN_PICKAXE;
    }

    @Override
    public Text name() {
        return Translations.guiPick;
    }

    @Override
    public List<StatisticEntry> screenAttributes() {
        return List.of(
            new StatisticEntry(this.statistic(StatisticType.efficiency), Translations.guiToolEfficiency.format(this.formatStatistic(StatisticType.efficiency))),
            new StatisticEntry(this.statistic(StatisticType.miningLevel), Translations.guiMiningLevel.format(this.formatStatistic(StatisticType.miningLevel), this.miningLevelName())),
            new StatisticEntry(this.statistic(StatisticType.reach), Translations.guiReach.format(this.formatStatistic(StatisticType.reach)))
        );
    }

    @Override
    public List<Text> tooltip() {
        var format = DecimalFormat.getInstance();

        return List.of(
            Translations.tooltipReach.translate(format.format(this.doubleValue(StatisticType.reach))),
            Translations.tooltipToolEfficiency.translate(format.format(this.doubleValue(StatisticType.efficiency))),
            Translations.tooltipMiningLevel.translate(format.format(this.doubleValue(StatisticType.miningLevel))),
            LiteralText.EMPTY,
            LiteralText.EMPTY
        );
    }

    @Override
    public double increase(StatisticType statistic) {
        if (statistic == StatisticType.efficiency) return 0.5;
        if (statistic == StatisticType.reach) return 0.1;
        if (statistic == StatisticType.miningLevel) return 0.2;

        return 0;
    }
}
