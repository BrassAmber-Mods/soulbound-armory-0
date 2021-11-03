package user11681.soulboundarmory.skill.weapon.staff;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.skill.Skill;

public class EndermanacleSkill extends Skill {
    public EndermanacleSkill(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return learned ? level + 1 : 3;
    }
}
