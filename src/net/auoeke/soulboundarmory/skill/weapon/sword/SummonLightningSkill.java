package net.auoeke.soulboundarmory.skill.weapon.sword;

import net.auoeke.soulboundarmory.skill.Skill;
import net.minecraft.util.Identifier;

public class SummonLightningSkill extends Skill {
    public SummonLightningSkill(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return 3;
    }
}
