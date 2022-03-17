package soulboundarmory.skill.tool.common;

import soulboundarmory.client.gui.screen.SoulboundTab;
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
    public void render(SoulboundTab tab, int level, int x, int y) {
        tab.renderGuiItem(Items.ENDER_PEARL.getDefaultStack(), x, y, 0);
    }
}
