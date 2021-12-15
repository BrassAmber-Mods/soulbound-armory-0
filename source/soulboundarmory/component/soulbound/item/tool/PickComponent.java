package soulboundarmory.component.soulbound.item.tool;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraftforge.common.ToolActions;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.registry.SoulboundItems;

public class PickComponent extends ToolComponent<PickComponent> {
    public PickComponent(SoulboundComponent<?> component) {
        super(component);

        this.statistics
            .category(Category.datum, StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
            .category(Category.attribute, StatisticType.efficiency, StatisticType.reach, StatisticType.upgradeProgress)
            .min(2, StatisticType.reach)
            .max(0, StatisticType.upgradeProgress);

        this.enchantments.add(enchantment -> Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains));
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
    public List<Text> tooltip() {
        var format = DecimalFormat.getInstance();

        return List.of(
            Translations.tooltipReach.translate(format.format(this.doubleValue(StatisticType.reach))),
            Translations.tooltipToolEfficiency.translate(format.format(this.doubleValue(StatisticType.efficiency))),
            Translations.tooltipUpgradeProgress.translate(format.format(this.doubleValue(StatisticType.upgradeProgress))),
            LiteralText.EMPTY,
            LiteralText.EMPTY
        );
    }

    @Override
    public double increase(StatisticType statistic) {
        if (statistic == StatisticType.efficiency) return 0.5;
        if (statistic == StatisticType.reach) return 0.1;
        if (statistic == StatisticType.upgradeProgress) return 0.2;

        return 0;
    }

    @Override
    protected Tag<Block> tag() {
        return BlockTags.PICKAXE_MINEABLE;
    }

    @Override
    protected boolean canAbsorb(ItemStack stack) {
        return stack.canPerformAction(ToolActions.PICKAXE_DIG);
    }
}
