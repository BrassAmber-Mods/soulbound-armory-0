package soulboundarmory.event;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.particle.DamageParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.render.SoulboundDaggerEntityRenderer;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemMarkerComponent;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.item.SoulboundItem;
import soulboundarmory.particle.UnlockParticle;
import soulboundarmory.util.Util;

@EventBusSubscriber(value = Dist.CLIENT, modid = SoulboundArmory.ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientModEvents {
    @SubscribeEvent public static void registerTooltipComponentFactory(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ItemMarkerComponent.class, ItemMarkerComponent::tooltip);
    }

    @SubscribeEvent public static void clientSetup(FMLClientSetupEvent event) {
        ForgeRegistries.ITEMS.getValues().stream().filter(SoulboundItem.class::isInstance).forEach(item -> {
                ModelPredicateProviderRegistry.register(
                    item,
                    Util.id("animating"),
                    (stack, world, holder, entityID) -> Components.marker.optional(stack)
                        .filter(ItemMarkerComponent::animating)
                        .or(() -> Components.entityData.optional(holder).flatMap(data -> data.unlockedStack).filter(marker -> marker.animating() && marker.item() != null && marker.item().accepts(stack)))
                        .isPresent() ? 1 : 0
                );

                ModelPredicateProviderRegistry.register(
                    item,
                    Util.id("level"),
                    (stack, world, holder, entityID) -> Components.marker.optional(stack).flatMap(ItemMarkerComponent::optionalItem).map(ItemComponent::level).orElse(0)
                );
            }
        );
    }

    @SubscribeEvent public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SoulboundDaggerEntity.type, SoulboundDaggerEntityRenderer::new);
    }

    @SubscribeEvent public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.register(SoulboundArmory.criticalHitParticleType, DamageParticle.Factory::new);
        event.register(SoulboundArmory.unlockParticle, UnlockParticle.Factory::new);
    }
}
