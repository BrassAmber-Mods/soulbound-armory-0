package soulboundarmory.skill.tool.common;

import soulboundarmory.skill.Skill;

public final class AmbidexteritySkill extends Skill {
    public AmbidexteritySkill() {
        super("ambidexterity");
    }

    @Override
    public int cost(boolean learned, int level) {
        return 5;
    }
}
