package user11681.soulboundarmory.skill.weapon.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.cell.client.gui.screen.CellScreen;
import user11681.soulboundarmory.skill.Skill;

public class NourishmentSkill extends Skill {
    public NourishmentSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return !learned ? 3 : level + 1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CellScreen screen, MatrixStack matrices, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(new ItemStack(Items.COOKED_BEEF), x, y, zOffset);
    }
}
