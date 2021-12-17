package soulboundarmory.skill.weapon.dagger;

import cell.client.gui.screen.CellScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import soulboundarmory.skill.Skill;
public class ThrowingSkill extends Skill {
    public ThrowingSkill() {
        super("throwing", 1);
    }

    @Override
    public int cost(int level) {
        return 2;
    }

    @Override
    public void render(CellScreen screen, MatrixStack matrixes, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(Items.TRIDENT.getDefaultStack(), x, y, zOffset);
    }
}
