package user11681.soulboundarmory.skill.weapon.sword;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.skill.Skill;

public class SummonLightningSkill extends Skill {
    public SummonLightningSkill(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return 3;
    }
}
