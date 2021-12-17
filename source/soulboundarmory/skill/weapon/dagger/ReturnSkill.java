package soulboundarmory.skill.weapon.dagger;

import soulboundarmory.registry.Skills;
import soulboundarmory.skill.Skill;

public class ReturnSkill extends Skill {
    public ReturnSkill() {
        super("return", 1);
    }

    @Override
    public void initDependencies() {
        this.dependencies.add(Skills.throwing);

        super.initDependencies();
    }

    @Override
    public int cost(int level) {
        return 2;
    }
}
