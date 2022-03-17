package soulboundarmory.skill.weapon.common;

import soulboundarmory.client.gui.screen.SoulboundTab;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import soulboundarmory.skill.Skill;

public final class NourishmentSkill extends Skill {
    public NourishmentSkill() {
        super("nourishment");
    }

    @Override
    public int cost(int level) {
        return level == 1 ? 3 : level;
    }

    @Override
    public void render(SoulboundTab tab, int level, int x, int y) {
        tab.renderGuiItem(new ItemStack(Items.COOKED_BEEF), x, y, 0);
    }
}
