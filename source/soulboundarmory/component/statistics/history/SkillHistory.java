package soulboundarmory.component.statistics.history;

import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.skill.Skill;

public final class SkillHistory extends History<SkillRecord> {
	public SkillHistory(ItemComponent<?> component) {
		super(component);
	}

	public void record(Skill skill, int points) {
		this.record(new SkillRecord(this.component, skill, points));
	}

	@Override
	protected SkillRecord skeleton() {
		return new SkillRecord(this.component);
	}
}
