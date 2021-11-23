package net.auoeke.soulboundarmory.skill.weapon.greatsword;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.auoeke.cell.client.gui.screen.CellScreen;
import net.auoeke.soulboundarmory.registry.Skills;
import net.auoeke.soulboundarmory.skill.Skill;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FreezingSkill extends Skill {
    public FreezingSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public void initDependencies() {
        this.dependencies.add(Skills.leaping);

        super.initDependencies();
    }

    @Override
    public int cost(boolean learned, int level) {
        return 2;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CellScreen screen, MatrixStack matrices, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(Items.SNOWBALL.getDefaultInstance(), x, y, zOffset);
    }
}
