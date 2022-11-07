package soulboundarmory.component.soulbound.item.weapon;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.item.SoulboundItems;
import soulboundarmory.skill.Skills;
import soulboundarmory.util.Math2;
import soulboundarmory.util.Util;

public class GreatswordComponent extends WeaponComponent<GreatswordComponent> {
	public int leapDuration;

	protected NbtCompound cannotFreeze = new NbtCompound();
	protected float zenith;
	protected float leapForce;

	public GreatswordComponent(MasterComponent<?> component) {
		super(component);

		this.statistics
			.statistics(StatisticType.experience, StatisticType.level, StatisticType.skillPoints, StatisticType.attributePoints, StatisticType.enchantmentPoints)
			.statistics(StatisticType.efficiency)
			.constant(6, StatisticType.reach)
			.min(0.8, StatisticType.attackSpeed)
			.min(4, StatisticType.attackDamage);

		this.enchantments.initialize(enchantment -> Stream.of("soulbound", "holding", "smelt").noneMatch(enchantment.getTranslationKey().toLowerCase()::contains));
		this.skills.add(Skills.circumspection, Skills.precision, Skills.nourishment, Skills.leaping, Skills.freezing);
	}

	@Override
	public ItemComponentType<GreatswordComponent> type() {
		return ItemComponentType.greatsword;
	}

	@Override
	public Item item() {
		return SoulboundItems.greatsword;
	}

	@Override
	public Text name() {
		return Translations.guiGreatsword;
	}

	public float leapForce() {
		return this.leapForce;
	}

	public void leap(float force) {
		this.resetLeapForce();
		this.leapForce = force;
		this.zenith = (float) Math2.zenith(this.player);
	}

	public void resetLeapForce() {
		this.leapForce = 0;
		this.leapDuration = 0;
		this.zenith = 0;
		this.cannotFreeze = new NbtCompound();
	}

	public float zenith() {
		return this.zenith;
	}

	public boolean canFreeze(Entity entity) {
		return !(entity instanceof ItemEntity);
	}

	public void freeze(Entity entity, int ticks, double damage) {
		var component = Components.entityData.of(entity);
		var id = entity.getUuid();
		var key = id.toString();

		if (!this.cannotFreeze.contains(key) && component.canBeFrozen()) {
			component.freeze(this.player, this.leapForce, ticks, (float) damage);
			this.cannotFreeze.putUuid(key, id);
		}
	}

	@Override
	public List<StatisticType> screenAttributes() {
		return Util.add(super.screenAttributes(), StatisticType.efficiency);
	}

	@Override
	public double increase(StatisticType type) {
		if (type == StatisticType.attackSpeed) return 0.02;
		if (type == StatisticType.attackDamage) return 0.1;
		if (type == StatisticType.criticalHitRate) return 0.01;
		if (type == StatisticType.efficiency) return 0.02;

		return 0;
	}

	@Override
	public void tick() {
		if (this.leapDuration > 0) {
			if (--this.leapDuration == 0) {
				this.resetLeapForce();
			}
		}
	}

	@Override
	public void serialize(NbtCompound tag) {
		super.serialize(tag);

		tag.putInt("leapDuration", this.leapDuration);
		tag.putDouble("leapForce", this.leapForce());
		tag.put("cannotFreeze", this.cannotFreeze);
	}

	@Override
	public void deserialize(NbtCompound tag) {
		super.deserialize(tag);

		this.cannotFreeze = tag.getCompound("cannotFreeze");
	}
}
