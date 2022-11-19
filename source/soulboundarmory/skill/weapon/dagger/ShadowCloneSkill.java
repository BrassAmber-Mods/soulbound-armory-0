package soulboundarmory.skill.weapon.dagger;

import java.util.Collections;
import java.util.Set;
import soulboundarmory.skill.Skill;
import soulboundarmory.skill.Skills;

public class ShadowCloneSkill extends Skill {
	public ShadowCloneSkill() {
		super("shadow_clone", 1);
	}

	@Override public Set<Skill> dependencies() {
		return Collections.singleton(Skills.throwing);
	}

	@Override
	public int cost(int level) {
		return 2;
	}
}
