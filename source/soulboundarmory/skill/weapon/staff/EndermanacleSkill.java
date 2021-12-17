package soulboundarmory.skill.weapon.staff;

import soulboundarmory.skill.Skill;

public final class EndermanacleSkill extends Skill {
    public EndermanacleSkill() {
        super("endermanacle");
    }

    @Override
    public int cost(int level) {
        return level == 1 ? 3 : level;
    }
}
