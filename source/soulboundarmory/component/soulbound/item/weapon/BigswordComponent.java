package soulboundarmory.component.soulbound.item.weapon;

import java.util.stream.Stream;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.item.SoulboundItems;
import soulboundarmory.skill.Skills;

public class BigswordComponent extends WeaponComponent<BigswordComponent> {
	private int chargeDelay;

	public BigswordComponent(MasterComponent<?> component) {
		super(component);

		this.statistics
			.statistics(StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
			.statistics(StatisticType.efficiency)
			.min(4, StatisticType.attackDamage)
			.min(1, StatisticType.attackSpeed)
			.min(3, StatisticType.reach);

		this.enchantments.initialize(enchantment -> Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains));
		this.skills.add(Skills.circumspection, Skills.precision, Skills.nourishment);
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

	@Override public void tick() {
		super.tick();

		this.chargeDelay--;
	}

	public void charge() {
		this.chargeDelay = 16;
	}

	public boolean canCharge() {
		return this.chargeDelay <= 0;
	}
}
