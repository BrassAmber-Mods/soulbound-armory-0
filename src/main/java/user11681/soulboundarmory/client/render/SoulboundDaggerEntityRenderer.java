package user11681.soulboundarmory.client.render;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.entity.SoulboundDaggerEntity;

@OnlyIn(Dist.CLIENT)
public class SoulboundDaggerEntityRenderer extends ArrowRenderer<SoulboundDaggerEntity> {
    private static final ResourceLocation id = SoulboundArmory.id("textures/item/soulbound_dagger.png");

    public SoulboundDaggerEntityRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getTextureLocation(SoulboundDaggerEntity entity) {
        return id;
    }
}
