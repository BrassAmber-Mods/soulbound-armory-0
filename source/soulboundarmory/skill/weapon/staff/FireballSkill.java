package soulboundarmory.skill.weapon.staff;

import cell.client.gui.widget.Widget;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import soulboundarmory.skill.Skill;
public class FireballSkill extends Skill {
    public FireballSkill() {
        super("fireball", 1);
    }

    @Override
    public int cost(int level) {
        return 1;
    }

    @Override
    public void render(Widget<?> tab, int level, int x, int y) {
        tab.renderGuiItem(new ItemStack(Items.FIRE_CHARGE), x, y, 0);
    }
}
