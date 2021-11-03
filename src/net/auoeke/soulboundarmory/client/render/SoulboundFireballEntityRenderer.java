package net.auoeke.soulboundarmory.client.render;

import net.auoeke.soulboundarmory.entity.SoulboundFireballEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.util.Identifier;

public class SoulboundFireballEntityRenderer extends EntityRenderer<SoulboundFireballEntity> {
    private static final Identifier id = new Identifier("item/fire_charge.png");

    public SoulboundFireballEntityRenderer(EntityRenderDispatcher manager) {
        super(manager);
    }

    @Override
    public Identifier getTexture(SoulboundFireballEntity entity) {
        return id;
    }
}
