package soulboundarmory.skill.weapon.staff;

import soulboundarmory.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class EndermanacleSkill extends Skill {
    public EndermanacleSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return learned ? level + 1 : 3;
    }
}
