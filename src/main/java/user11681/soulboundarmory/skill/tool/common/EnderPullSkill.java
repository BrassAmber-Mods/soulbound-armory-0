package user11681.soulboundarmory.skill.tool.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.soulboundarmory.skill.Skill;

public class EnderPullSkill extends Skill {
    public EnderPullSkill(ResourceLocation identifier) {
        super(identifier);
    }

    @Override
    public int cost(boolean learned, final int level) {
        return 3;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(SpunScreen screen, final MatrixStack matrices, final int level, final int x, final int y, final int zOffset) {
        screen.renderGuiItem(Items.ENDER_PEARL.getStackForRender(), x, y, zOffset);
    }
}
