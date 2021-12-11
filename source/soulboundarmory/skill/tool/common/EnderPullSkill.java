package soulboundarmory.skill.tool.common;

import cell.client.gui.screen.CellScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import soulboundarmory.skill.Skill;

public class EnderPullSkill extends Skill {
    public EnderPullSkill(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return 3;
    }

    @Override
    public void render(CellScreen screen, MatrixStack matrixes, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(Items.ENDER_PEARL.getDefaultStack(), x, y, zOffset);
    }
}
