package user11681.soulboundarmory.skill.weapon.staff;

import net.minecraft.util.ResourceLocation;
import user11681.soulboundarmory.skill.Skill;

public class EndermanacleSkill extends Skill {
    public EndermanacleSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, final int level) {
        return learned ? level + 1 : 3;
    }
}
