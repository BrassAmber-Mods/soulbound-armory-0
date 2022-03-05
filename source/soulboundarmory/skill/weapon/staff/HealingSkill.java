package soulboundarmory.skill.weapon.staff;

import soulboundarmory.lib.gui.widget.Widget;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import soulboundarmory.skill.Skill;

public class HealingSkill extends Skill {
    public HealingSkill() {
        super("healing", 1);
    }

    @Override
    public int cost(int level) {
        return 2;
    }

    @Override
    public void render(Widget<?> tab, int level, int x, int y) {
        tab.renderGuiItem(PotionUtil.setPotion(Items.POTION.getDefaultStack(), Potions.HEALING), x, y, 0);
    }
}
