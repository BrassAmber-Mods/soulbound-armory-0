package soulboundarmory.skill.weapon.staff;

import soulboundarmory.skill.Skill;

public final class EndermanacleSkill extends Skill {
    public EndermanacleSkill() {
        super("endermanacle");
    }

    @Override
    public int cost(boolean learned, int level) {
        return learned ? level : 3;
    }
}
