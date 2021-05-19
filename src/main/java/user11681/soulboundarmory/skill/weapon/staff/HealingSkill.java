package user11681.soulboundarmory.skill.weapon.staff;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.soulboundarmory.skill.Skill;
public class HealingSkill extends Skill {
    public HealingSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, final int level) {
            return 2;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(SpunScreen screen, final MatrixStack matrices, final int level, final int x, final int y, final int zOffset) {
        screen.renderGuiItem(PotionUtils.setPotion(Items.POTION.getDefaultInstance(), Potions.HEALING), x, y, zOffset);
    }
}
