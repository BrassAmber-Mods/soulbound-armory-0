package soulboundarmory.skill.tool.common;

import cell.client.gui.widget.Widget;
import net.minecraft.item.Items;
import soulboundarmory.skill.Skill;

public final class EnderPullSkill extends Skill {
    public EnderPullSkill() {
        super("ender_pull", 1);
    }

    @Override
    public int cost(int level) {
        return 3;
    }

    @Override
    public void render(Widget<?> tab, int level, int x, int y) {
        tab.renderGuiItem(Items.ENDER_PEARL.getDefaultStack(), x, y, 0);
    }
}
