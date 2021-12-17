package soulboundarmory.skill.weapon.common;

import soulboundarmory.skill.Skill;

public class PrecisionSkill extends Skill {
    public PrecisionSkill() {
        super("precision", 1);
    }

    @Override
    public int cost(int level) {
        return 1;
    }
}
