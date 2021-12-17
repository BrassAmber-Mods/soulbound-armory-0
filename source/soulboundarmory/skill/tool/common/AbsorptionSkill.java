package soulboundarmory.skill.tool.common;

import soulboundarmory.skill.Skill;

public final class AbsorptionSkill extends Skill {
    public AbsorptionSkill() {
        super("absorption", 1);
    }

    @Override
    public int cost(int level) {
        return 1;
    }
}
