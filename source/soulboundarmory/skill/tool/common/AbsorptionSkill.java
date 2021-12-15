package soulboundarmory.skill.tool.common;

import soulboundarmory.skill.Skill;

public final class AbsorptionSkill extends Skill {
    public AbsorptionSkill() {
        super("absorption");
    }

    @Override
    public int cost(boolean learned, int level) {
        return learned ? -1 : 1;
    }
}
