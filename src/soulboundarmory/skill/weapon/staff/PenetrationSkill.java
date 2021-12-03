package soulboundarmory.skill.weapon.staff;

import net.minecraft.util.Identifier;
import soulboundarmory.skill.Skill;

public class PenetrationSkill extends Skill {
    public PenetrationSkill(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return learned ? level + 1 : 2;
    }
}
