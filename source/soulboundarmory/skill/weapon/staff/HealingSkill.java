package soulboundarmory.skill.weapon.staff;

import cell.client.gui.screen.CellScreen;
import net.minecraft.client.util.math.MatrixStack;
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
    public void render(CellScreen screen, MatrixStack matrixes, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(PotionUtil.setPotion(Items.POTION.getDefaultStack(), Potions.HEALING), x, y, zOffset);
    }
}
