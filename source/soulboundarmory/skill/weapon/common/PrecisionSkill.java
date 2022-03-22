package soulboundarmory.skill.weapon.common;

import net.minecraft.item.Items;
import soulboundarmory.module.gui.widget.Widget;
import soulboundarmory.skill.Skill;

public class PrecisionSkill extends Skill {
    public PrecisionSkill() {
        super("precision", 1);
    }

    @Override
    public int cost(int level) {
        return 1;
    }

    @Override public void render(Widget<?> tab, int level) {
        tab.renderGuiItem(Items.DIAMOND_SWORD, tab.x(), tab.y());
    }
}
