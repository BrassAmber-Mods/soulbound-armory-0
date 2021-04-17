package user11681.soulboundarmory.client.render;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.entity.SoulboundFireballEntity;

public class SoulboundFireballEntityRenderer extends EntityRenderer<SoulboundFireballEntity> {
    private static final Identifier id = new Identifier("item/fire_charge.png");

    public SoulboundFireballEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(SoulboundFireballEntity entity) {
        return id;
    }
}
