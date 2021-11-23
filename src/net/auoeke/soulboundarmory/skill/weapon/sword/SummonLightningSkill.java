package net.auoeke.soulboundarmory.skill.weapon.sword;

import net.auoeke.soulboundarmory.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class SummonLightningSkill extends Skill {
    public SummonLightningSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return 3;
    }
}
