package user11681.soulboundarmory.skill.weapon.greatsword;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.soulboundarmory.skill.Skill;

public class LeapingSkill extends Skill {
    public LeapingSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, final int level) {
        return 1;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(SpunScreen screen, final MatrixStack matrices, final int level, final int x, final int y, final int zOffset) {
        screen.renderGuiItem(new ItemStack(Items.RABBIT_FOOT), x, y, zOffset);
    }
}
