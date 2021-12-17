package soulboundarmory.skill.weapon.common;

import cell.client.gui.screen.CellScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import soulboundarmory.skill.Skill;

public final class NourishmentSkill extends Skill {
    public NourishmentSkill() {
        super("nourishment");
    }

    @Override
    public int cost(int level) {
        return level == 1 ? 3 : level;
    }

    @Override
    public void render(CellScreen screen, MatrixStack matrixes, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(new ItemStack(Items.COOKED_BEEF), x, y, zOffset);
    }
}
