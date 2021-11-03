package user11681.soulboundarmory.skill.weapon.greatsword;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.cell.client.gui.screen.CellScreen;
import user11681.soulboundarmory.skill.Skill;

public class LeapingSkill extends Skill {
    public LeapingSkill(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return 1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CellScreen screen, MatrixStack matrices, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(new ItemStack(Items.RABBIT_FOOT), x, y, zOffset);
    }
}
