package soulboundarmory.skill.weapon.staff;

import cell.client.gui.screen.CellScreen;
import soulboundarmory.skill.Skill;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HealingSkill extends Skill {
    public HealingSkill(Identifier identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return 2;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CellScreen screen, MatrixStack matrices, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(PotionUtil.setPotion(Items.POTION.getDefaultStack(), Potions.HEALING), x, y, zOffset);
    }
}
