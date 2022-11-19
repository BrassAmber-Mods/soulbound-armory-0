package soulboundarmory.component.statistics.history;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.StatisticType;

public final class AttributeRecord extends Record {
	private StatisticType type;
	private int points;

	public AttributeRecord(ItemComponent<?> component, StatisticType type, int points) {
		super(component);

		this.type = type;
		this.points = points;
	}

	public AttributeRecord(ItemComponent<?> component) {
		super(component);
	}

	@Override
	public boolean revert(int level) {
		var change = this.component.level() - level;
		var deduction = Math.min(this.points, change);
		this.component.addAttribute(this.type, -deduction);
		this.points -= deduction;

		return this.points + deduction >= change;
	}

	@Override
	public void pop() {
		this.component.addAttribute(this.type, -this.points);
	}

	@Override
	public void deserialize(NbtCompound tag) {
		this.type = StatisticType.registry().getValue(new Identifier(tag.getString("attribute")));
		this.points = tag.getInt("points");
	}

	@Override
	public void serialize(NbtCompound tag) {
		tag.putString(this.type.string(), "attribute");
		tag.putInt("points", this.points);
	}
}
