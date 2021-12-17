package soulboundarmory.skill.tool.common;

import cell.client.gui.screen.CellScreen;
import net.minecraft.client.util.math.MatrixStack;
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
    public void render(CellScreen screen, MatrixStack matrixes, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(Items.ENDER_PEARL.getDefaultStack(), x, y, zOffset);
    }
}
