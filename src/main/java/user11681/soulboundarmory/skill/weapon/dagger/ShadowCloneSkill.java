package user11681.soulboundarmory.skill.weapon.dagger;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.skill.Skill;
public class ShadowCloneSkill extends Skill {
    public ShadowCloneSkill(final Identifier identifier) {
        super(identifier);
    }

    @Override
    public void initDependencies() {
        this.dependencies.add(Skills.THROWING);

        super.initDependencies();
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return 2;
    }
}
