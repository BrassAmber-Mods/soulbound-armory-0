package soulboundarmory.skill.weapon.dagger;

import net.minecraft.item.Items;
import soulboundarmory.module.gui.widget.Widget;
import soulboundarmory.skill.Skill;
public class ThrowingSkill extends Skill {
    public ThrowingSkill() {
        super("throwing", 1);
    }

    @Override
    public int cost(int level) {
        return 2;
    }

    @Override
    public void render(Widget<?> tab, int level) {
        tab.renderGuiItem(Items.TRIDENT, tab.absoluteX(), tab.absoluteY());
    }
}
