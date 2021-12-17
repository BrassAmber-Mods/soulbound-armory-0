package soulboundarmory.skill.weapon.dagger;

import soulboundarmory.registry.Skills;
import soulboundarmory.skill.Skill;

public class ShadowCloneSkill extends Skill {
    public ShadowCloneSkill() {
        super("shadow_clone", 1);
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
