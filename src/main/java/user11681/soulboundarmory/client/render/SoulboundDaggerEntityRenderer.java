package user11681.soulboundarmory.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.entity.SoulboundDaggerEntity;

@Environment(EnvType.CLIENT)
public class SoulboundDaggerEntityRenderer extends ProjectileEntityRenderer<SoulboundDaggerEntity> {
    public static final Identifier id = SoulboundArmory.id("textures/item/soulbound_dagger.png");

    public SoulboundDaggerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(SoulboundDaggerEntity entity) {
        return id;
    }
}
