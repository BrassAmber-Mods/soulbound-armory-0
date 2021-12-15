package soulboundarmory.client.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.entity.SoulboundDaggerEntity;

public class SoulboundDaggerEntityRenderer extends ProjectileEntityRenderer<SoulboundDaggerEntity> {
    private static final Identifier id = SoulboundArmory.id("textures/item/soulbound_dagger.png");

    public SoulboundDaggerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(SoulboundDaggerEntity entity) {
        return id;
    }
}
