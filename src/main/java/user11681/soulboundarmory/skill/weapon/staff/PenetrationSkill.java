package user11681.soulboundarmory.skill.weapon.staff;

import net.minecraft.util.ResourceLocation;
import user11681.soulboundarmory.skill.Skill;

public class PenetrationSkill extends Skill {
    public PenetrationSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return learned ? level + 1 : 2;
    }
}
