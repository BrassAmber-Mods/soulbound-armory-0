package user11681.soulboundarmory.client.render;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.entity.SoulboundDaggerEntity;

@OnlyIn(Dist.CLIENT)
public class SoulboundDaggerEntityRenderer extends ProjectileEntityRenderer<SoulboundDaggerEntity> {
    private static final Identifier id = SoulboundArmory.id("textures/item/soulbound_dagger.png");

    public SoulboundDaggerEntityRenderer(EntityRenderDispatcher manager) {
        super(manager);
    }

    @Override
    public Identifier getTexture(SoulboundDaggerEntity entity) {
        return id;
    }
}
