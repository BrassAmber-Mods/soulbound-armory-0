package user11681.soulboundarmory.skill.weapon.greatsword;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.registry.Skills;
import user11681.soulboundarmory.skill.Skill;
public class FreezingSkill extends Skill {
    public FreezingSkill(final Identifier identifier) {
        super(identifier);
    }

    @Override
    public void initDependencies() {
        this.dependencies.add(Skills.LEAPING);

        super.initDependencies();
    }

    @Override
    public int getCost(final boolean learned, final int level) {
        return 2;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(final SpunScreen screen, final MatrixStack matrices, final int level, final int x, final int y, final int zOffset) {
        screen.renderGuiItem(new ItemStack(Items.SNOWBALL), x, y, zOffset);
    }
}
