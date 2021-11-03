package user11681.soulboundarmory.skill.tool.common;

import net.minecraft.util.Identifier;
import user11681.soulboundarmory.skill.Skill;

public class AmbidexteritySkill extends Skill {
    public AmbidexteritySkill(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return 5;
    }
}
