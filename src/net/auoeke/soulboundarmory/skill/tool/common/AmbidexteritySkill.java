package net.auoeke.soulboundarmory.skill.tool.common;

import net.auoeke.soulboundarmory.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class AmbidexteritySkill extends Skill {
    public AmbidexteritySkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return 5;
    }
}
