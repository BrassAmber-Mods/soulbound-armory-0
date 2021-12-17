package soulboundarmory.skill.tool.common;

import soulboundarmory.skill.Skill;

public final class CircumspectionSkill extends Skill {
    public CircumspectionSkill() {
        super("circumspection", 1);
    }

    @Override
    public int cost(int level) {
        return 1;
    }
}
