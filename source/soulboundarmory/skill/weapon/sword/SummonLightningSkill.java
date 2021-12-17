package soulboundarmory.skill.weapon.sword;

import soulboundarmory.skill.Skill;

public class SummonLightningSkill extends Skill {
    public SummonLightningSkill() {
        super("summon_lightning", 1);
    }

    @Override
    public int cost(int level) {
        return 3;
    }
}
