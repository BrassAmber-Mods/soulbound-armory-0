package soulboundarmory.skill.weapon.greatsword;

import cell.client.gui.screen.CellScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.registry.Skills;
import soulboundarmory.skill.Skill;

public class FreezingSkill extends Skill {
    public FreezingSkill() {
        super("freezing");
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
    public void render(CellScreen screen, MatrixStack matrixes, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(Items.SNOWBALL.getDefaultStack(), x, y, zOffset);
    }
}
