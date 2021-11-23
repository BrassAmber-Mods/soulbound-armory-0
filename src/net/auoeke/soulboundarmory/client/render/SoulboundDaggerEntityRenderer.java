package net.auoeke.soulboundarmory.client.render;

import net.auoeke.soulboundarmory.SoulboundArmory;
import net.auoeke.soulboundarmory.entity.SoulboundDaggerEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
