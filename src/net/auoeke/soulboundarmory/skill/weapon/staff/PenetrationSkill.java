package net.auoeke.soulboundarmory.skill.weapon.staff;

import net.auoeke.soulboundarmory.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class PenetrationSkill extends Skill {
    public PenetrationSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return learned ? level + 1 : 2;
    }
}
