package soulboundarmory.skill.tool.common;

import soulboundarmory.skill.Skill;

public final class AmbidexteritySkill extends Skill {
    public AmbidexteritySkill() {
        super("ambidexterity", 1);
    }

    @Override
    public int cost(int level) {
        return 5;
    }
}
