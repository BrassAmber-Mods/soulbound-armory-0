package net.auoeke.soulboundarmory.skill.weapon.greatsword;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.auoeke.cell.client.gui.screen.CellScreen;
import net.auoeke.soulboundarmory.skill.Skill;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LeapingSkill extends Skill {
    public LeapingSkill(ResourceLocation identifier) {
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
