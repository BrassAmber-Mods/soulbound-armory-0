package soulboundarmory.client.render;

import soulboundarmory.SoulboundArmory;
import soulboundarmory.entity.SoulboundDaggerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulboundDaggerEntityRenderer extends ProjectileEntityRenderer<SoulboundDaggerEntity> {
    private static final Identifier id = SoulboundArmory.id("textures/item/soulbound_dagger.png");

    public SoulboundDaggerEntityRenderer(EntityRenderDispatcher manager) {
        super(manager);
    }

    @Override
    public Identifier getEntityTexture(SoulboundDaggerEntity entity) {
        return id;
    }
}
