package soulboundarmory.skill.tool.common;

import net.minecraft.item.Items;
import soulboundarmory.module.gui.widget.Widget;
import soulboundarmory.skill.Skill;

public final class CircumspectionSkill extends Skill {
    public CircumspectionSkill() {
        super("circumspection", 1);
    }

    @Override
    public int cost(int level) {
        return 1;
    }

    @Override public void render(Widget<?> tab, int level) {
        tab.renderGuiItem(Items.GLASS, tab.absoluteX(), tab.absoluteY());
    }
}
