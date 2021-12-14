package soulboundarmory.skill.weapon.greatsword;

import cell.client.gui.screen.CellScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.skill.Skill;

public class LeapingSkill extends Skill {
    public LeapingSkill() {
        super("leaping");
    }

    @Override
    public int cost(boolean learned, int level) {
        return 1;
    }

    @Override
    public void render(CellScreen screen, MatrixStack matrixes, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(new ItemStack(Items.RABBIT_FOOT), x, y, zOffset);
    }
}
