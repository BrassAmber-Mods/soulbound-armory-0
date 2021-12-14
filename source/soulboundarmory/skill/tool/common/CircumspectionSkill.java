package soulboundarmory.skill.tool.common;

import soulboundarmory.skill.Skill;

public class CircumspectionSkill extends Skill {
    public CircumspectionSkill() {
        super("circumspection");
    }

    @Override
    public int cost(boolean learned, int level) {
        return learned ? -1 : 1;
    }
}
