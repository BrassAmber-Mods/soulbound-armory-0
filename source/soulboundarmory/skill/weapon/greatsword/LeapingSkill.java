package soulboundarmory.skill.weapon.greatsword;

import net.minecraft.item.Items;
import soulboundarmory.lib.gui.widget.Widget;
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
    public void render(Widget<?> tab, int level) {
        tab.renderGuiItem(Items.RABBIT_FOOT, tab.x(), tab.y());
    }
}
