package soulboundarmory.skill.weapon.dagger;

import java.util.Collections;
import java.util.Set;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.module.gui.widget.Widget;
import soulboundarmory.skill.Skills;
import soulboundarmory.skill.Skill;

public class SneakReturnSkill extends Skill {
    public SneakReturnSkill() {
        super("sneak_return", 1);
    }

    @Override public Set<Skill> dependencies() {
        return Collections.singleton(Skills.returning);
    }

    @Override
    public int cost(int level) {
        return 1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(Widget<?> tab, int level) {
        tab.renderGuiItem(Items.LEAD, tab.absoluteX(), tab.absoluteY());
    }
}
