package soulboundarmory.skill.weapon.staff;

import cell.client.gui.screen.CellScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.skill.Skill;
public class FireballSkill extends Skill {
    public FireballSkill() {
        super("fireball");
    }

    @Override
    public int cost(boolean learned, int level) {
        return 1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CellScreen screen, MatrixStack matrixes, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(new ItemStack(Items.FIRE_CHARGE), x, y, zOffset);
    }
}
