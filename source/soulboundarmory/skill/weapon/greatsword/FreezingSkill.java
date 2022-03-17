package soulboundarmory.skill.weapon.greatsword;

import java.util.Collections;
import java.util.Set;
import soulboundarmory.client.gui.screen.SoulboundTab;
import net.minecraft.item.Items;
import soulboundarmory.skill.Skills;
import soulboundarmory.skill.Skill;

public class FreezingSkill extends Skill {
    public FreezingSkill() {
        super("freezing", 1);
    }

    @Override
    public Set<Skill> dependencies() {
        return Collections.singleton(Skills.leaping);
    }

    @Override
    public int cost(int level) {
        return 2;
    }

    @Override
    public void render(SoulboundTab tab, int level, int x, int y) {
        tab.renderGuiItem(Items.SNOWBALL.getDefaultStack(), x, y, 0);
    }
}
