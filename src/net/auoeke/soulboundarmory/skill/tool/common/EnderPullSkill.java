package net.auoeke.soulboundarmory.skill.tool.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.auoeke.cell.client.gui.screen.CellScreen;
import net.auoeke.soulboundarmory.skill.Skill;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnderPullSkill extends Skill {
    public EnderPullSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return 3;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CellScreen screen, MatrixStack matrices, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(Items.ENDER_PEARL.getDefaultInstance(), x, y, zOffset);
    }
}
