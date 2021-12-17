package soulboundarmory.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.util.Util;

public class SoulboundDaggerEntityRenderer extends ProjectileEntityRenderer<SoulboundDaggerEntity> {
    private static final Identifier id = Util.id("textures/item/dagger.png");

    public SoulboundDaggerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(SoulboundDaggerEntity entity) {
        return id;
    }
}
