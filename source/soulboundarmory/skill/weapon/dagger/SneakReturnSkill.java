package soulboundarmory.skill.weapon.dagger;

import java.util.Collections;
import java.util.Set;
import soulboundarmory.client.gui.screen.SoulboundTab;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    public void render(SoulboundTab tab, int level, int x, int y) {
        tab.renderGuiItem(Items.LEAD.getDefaultStack(), x, y, 0);
    }
}
