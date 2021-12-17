package soulboundarmory.skill.weapon.staff;

import soulboundarmory.skill.Skill;

public class PenetrationSkill extends Skill {
    public PenetrationSkill() {
        super("penetration");
    }

    @Override
    public int cost(int level) {
        return level == 1 ? 2 : level;
    }
}
