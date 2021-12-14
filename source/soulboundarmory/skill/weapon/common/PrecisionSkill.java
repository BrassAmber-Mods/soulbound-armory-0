package soulboundarmory.skill.weapon.common;

import soulboundarmory.skill.Skill;

public class PrecisionSkill extends Skill {
    public PrecisionSkill() {
        super("precision");
    }

    @Override
    public int cost(boolean learned, int level) {
        return learned ? -1 : 1;
    }
}
