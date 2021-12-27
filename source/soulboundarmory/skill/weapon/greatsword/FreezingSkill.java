package soulboundarmory.skill.weapon.greatsword;

import cell.client.gui.widget.Widget;
import net.minecraft.item.Items;
import soulboundarmory.registry.Skills;
import soulboundarmory.skill.Skill;

public class FreezingSkill extends Skill {
    public FreezingSkill() {
        super("freezing", 1);
    }

    @Override
    public void initDependencies() {
        this.dependencies.add(Skills.leaping);

        super.initDependencies();
    }

    @Override
    public int cost(int level) {
        return 2;
    }

    @Override
    public void render(Widget<?> tab, int level, int x, int y) {
        tab.renderGuiItem(Items.SNOWBALL.getDefaultStack(), x, y, 0);
    }
}
