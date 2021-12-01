package soulboundarmory.skill.weapon.dagger;

import com.mojang.blaze3d.matrix.MatrixStack;
import cell.client.gui.screen.CellScreen;
import soulboundarmory.registry.Skills;
import soulboundarmory.skill.Skill;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SneakReturnSkill extends Skill {
    public SneakReturnSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public void initDependencies() {
        this.dependencies.add(Skills.returning);

        super.initDependencies();
    }

    @Override
    public int cost(boolean learned, int level) {
        return 1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CellScreen screen, MatrixStack matrices, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(Items.LEAD.getDefaultInstance(), x, y, zOffset);
    }
}
