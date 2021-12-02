package soulboundarmory.skill.weapon.staff;

import com.mojang.blaze3d.matrix.MatrixStack;
import cell.client.gui.screen.CellScreen;
import soulboundarmory.skill.Skill;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HealingSkill extends Skill {
    public HealingSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, int level) {
        return 2;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(CellScreen screen, MatrixStack matrices, int level, int x, int y, int zOffset) {
        screen.renderGuiItem(PotionUtils.addPotionToItemStack(Items.POTION.getDefaultInstance(), Potions.HEALING), x, y, zOffset);
    }
}
