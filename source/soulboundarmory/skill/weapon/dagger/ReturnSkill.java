package soulboundarmory.skill.weapon.dagger;

import java.util.Collections;
import java.util.Set;
import soulboundarmory.skill.Skill;
import soulboundarmory.skill.Skills;

public class ReturnSkill extends Skill {
	public ReturnSkill() {
		super("return", 1);
	}

	@Override public Set<Skill> dependencies() {
		return Collections.singleton(Skills.throwing);
	}

	@Override
	public int cost(int level) {
		return 2;
	}
}
