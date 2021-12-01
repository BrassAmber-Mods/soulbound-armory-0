package soulboundarmory.client.render;

import soulboundarmory.entity.SoulboundFireballEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SoulboundFireballEntityRenderer extends EntityRenderer<SoulboundFireballEntity> {
    private static final ResourceLocation id = new ResourceLocation("item/fire_charge.png");

    public SoulboundFireballEntityRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getTextureLocation(SoulboundFireballEntity entity) {
        return id;
    }
}
