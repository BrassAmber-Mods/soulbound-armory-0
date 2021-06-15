package user11681.soulboundarmory.skill.weapon.dagger;

import net.minecraft.util.ResourceLocation;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.skill.Skill;

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
