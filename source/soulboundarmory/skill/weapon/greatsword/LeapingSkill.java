package soulboundarmory.skill.weapon.greatsword;

import soulboundarmory.client.gui.screen.SoulboundTab;
import net.minecraft.item.Items;
import soulboundarmory.skill.Skill;

public class LeapingSkill extends Skill {
    public LeapingSkill() {
        super("leaping", 1);
    }

    @Override
    public int cost(int level) {
        return 3;
    }

    @Override
    public void render(SoulboundTab tab, int level, int x, int y) {
        tab.renderGuiItem(Items.RABBIT_FOOT.getDefaultStack(), x, y, 0);
    }
}
