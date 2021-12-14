package soulboundarmory.skill.weapon.staff;

import soulboundarmory.skill.Skill;

public class PenetrationSkill extends Skill {
    public PenetrationSkill() {
        super("penetration");
    }

    @Override
    public int cost(boolean learned, int level) {
        return learned ? level : 2;
    }
}
