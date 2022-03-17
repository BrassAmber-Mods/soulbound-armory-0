package soulboundarmory.skill.weapon.dagger;

import soulboundarmory.client.gui.screen.SoulboundTab;
import net.minecraft.item.Items;
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
    public void render(SoulboundTab tab, int level, int x, int y) {
        tab.renderGuiItem(Items.TRIDENT.getDefaultStack(), x, y, 0);
    }
}
