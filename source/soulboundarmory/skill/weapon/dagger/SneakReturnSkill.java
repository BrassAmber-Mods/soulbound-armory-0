package soulboundarmory.skill.weapon.dagger;

import cell.client.gui.screen.CellScreen;
import soulboundarmory.registry.Skills;
import soulboundarmory.skill.Skill;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SneakReturnSkill extends Skill {
    public SneakReturnSkill(Identifier identifier) {
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
    public void render(CellScreen screen, MatrixStack matrixes, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(Items.LEAD.getDefaultStack(), x, y, zOffset);
    }
}
