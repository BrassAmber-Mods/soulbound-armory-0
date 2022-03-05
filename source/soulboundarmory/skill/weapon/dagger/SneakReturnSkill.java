package soulboundarmory.skill.weapon.dagger;

import soulboundarmory.lib.gui.widget.Widget;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.registry.Skills;
import soulboundarmory.skill.Skill;

public class SneakReturnSkill extends Skill {
    public SneakReturnSkill() {
        super("sneak_return", 1);
    }

    @Override
    public void initDependencies() {
        this.dependencies.add(Skills.returning);

        super.initDependencies();
    }

    @Override
    public int cost(int level) {
        return 1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(Widget<?> tab, int level, int x, int y) {
        tab.renderGuiItem(Items.LEAD.getDefaultStack(), x, y, 0);
    }
}
