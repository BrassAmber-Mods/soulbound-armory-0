package soulboundarmory.skill.weapon.dagger;

import soulboundarmory.registry.Skills;
import soulboundarmory.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class ShadowCloneSkill extends Skill {
    public ShadowCloneSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public void initDependencies() {
        this.dependencies.add(Skills.throwing);

        super.initDependencies();
    }

    @Override
    public int cost(boolean learned, int level) {
        return 2;
    }
}
