package soulboundarmory.skill.weapon.sword;

import soulboundarmory.skill.Skill;

public class SummonLightningSkill extends Skill {
    public SummonLightningSkill() {
        super("summon_lightning");
    }

    @Override
    public int cost(boolean learned, int level) {
        return 3;
    }
}
