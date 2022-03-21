package soulboundarmory.skill.weapon.common;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import soulboundarmory.lib.gui.widget.Widget;
import soulboundarmory.skill.Skill;

public final class NourishmentSkill extends Skill {
    public NourishmentSkill() {
        super("nourishment", 3);
    }

    @Override
    public int cost(int level) {
        return level == 1 ? 3 : level;
    }

    @Override
    public void render(Widget<?> tab, int level) {
        tab.renderGuiItem(new ItemStack(Items.COOKED_BEEF), tab.x(), tab.y());
    }
}
