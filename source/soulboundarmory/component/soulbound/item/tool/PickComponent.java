package soulboundarmory.component.soulbound.item.tool;

import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraftforge.common.ToolActions;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.item.SoulboundItems;

public class PickComponent extends ToolComponent<PickComponent> {
	public PickComponent(MasterComponent<?> component) {
		super(component);

		this.statistics
			.statistics(StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
			.statistics(StatisticType.efficiency)
			.constant(2, StatisticType.attackDamage)
			.constant(1.2, StatisticType.attackSpeed)
			.min(2, StatisticType.reach)
			.max(0, StatisticType.upgradeProgress);

		this.enchantments.initialize(enchantment -> Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains));
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
	public double increase(StatisticType type) {
		if (type == StatisticType.efficiency) return 0.5;
		if (type == StatisticType.reach) return 0.1;
		if (type == StatisticType.upgradeProgress) return 0.2;

		return 0;
	}

	@Override
	protected TagKey<Block> tag() {
		return BlockTags.PICKAXE_MINEABLE;
	}

	@Override
	protected boolean canAbsorb(ItemStack stack) {
		return stack.canPerformAction(ToolActions.PICKAXE_DIG);
	}
}
