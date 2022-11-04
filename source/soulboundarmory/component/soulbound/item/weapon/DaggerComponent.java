package soulboundarmory.component.soulbound.item.weapon;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.item.SoulboundItems;
import soulboundarmory.skill.Skills;
import soulboundarmory.util.Util;

public class DaggerComponent extends WeaponComponent<DaggerComponent> {
	public DaggerComponent(MasterComponent<?> component) {
		super(component);

		this.statistics
			.statistics(StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
			.statistics(StatisticType.efficiency)
			.constant(2, StatisticType.reach)
			.min(2, StatisticType.attackSpeed, StatisticType.attackDamage);

		this.enchantments.initialize(enchantment -> Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains));
		this.skills.add(Skills.circumspection, Skills.precision, Skills.nourishment, Skills.throwing, Skills.shadowClone, Skills.returning, Skills.sneakReturn);
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
	public List<StatisticType> screenAttributes() {
		return Util.add(super.screenAttributes(), StatisticType.efficiency);
	}

	@Override public boolean isEnabled() {
		return Configuration.Items.dagger;
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
