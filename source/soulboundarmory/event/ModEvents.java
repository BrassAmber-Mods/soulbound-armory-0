package soulboundarmory.event;

import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.NewRegistryEvent;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.gui.bar.ExperienceBar;
import soulboundarmory.client.render.SoulboundDaggerEntityRenderer;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.soulbound.item.ItemMarkerComponent;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.item.SoulboundItem;
import soulboundarmory.item.SoulboundItems;
import soulboundarmory.lib.gui.CellElement;
import soulboundarmory.particle.CriticalHitParticle;
import soulboundarmory.particle.UnlockParticle;
import soulboundarmory.skill.Skills;
import soulboundarmory.util.Util;

@EventBusSubscriber(modid = SoulboundArmory.ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEvents {
    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        if (Util.isPhysicalClient) {
            CellElement.client.particleManager.registerFactory(SoulboundArmory.criticalHitParticleType, CriticalHitParticle.Factory::new);
            CellElement.client.particleManager.registerFactory(SoulboundArmory.unlockParticle, UnlockParticle.Factory::new);
        }
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        MinecraftForgeClient.registerTooltipComponentFactory(ItemMarkerComponent.class, component -> new ExperienceBar().item(component.item).width(144));

        ForgeRegistries.ITEMS.getValues().stream().filter(SoulboundItem.class::isInstance).forEach(item -> {
                ModelPredicateProviderRegistry.register(
                    item,
                    Util.id("animating"),
                    (stack, world, holder, entityID) -> Components.marker.nullable(stack)
                        .filter(ItemMarkerComponent::animating)
                        .or(() -> Components.entityData.nullable(holder).flatMap(data -> data.unlockedStack).filter(marker -> marker.animating() && marker.item != null && marker.item.accepts(stack)))
                        .isPresent() ? 1 : 0
                );

                ModelPredicateProviderRegistry.register(
                    item,
                    Util.id("level"),
                    (stack, world, holder, entityID) -> Components.marker.nullable(stack).flatMap(ItemMarkerComponent::item).map(ItemComponent::level).orElse(0)
                );
            }
        );
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SoulboundDaggerEntity.type, SoulboundDaggerEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        Unsafe.ensureClassInitialized(ItemComponentType.class);
        Unsafe.ensureClassInitialized(Category.class);
        Unsafe.ensureClassInitialized(StatisticType.class);
        Unsafe.ensureClassInitialized(Skills.class);
        Unsafe.ensureClassInitialized(Components.class);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(SoulboundItems.dagger, SoulboundItems.sword, SoulboundItems.greatsword, SoulboundItems.bigsword, SoulboundItems.trident, SoulboundItems.pick);
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(SoulboundDaggerEntity.type.setRegistryName("dagger"));
    }

    @SubscribeEvent
    public static void registerParticle(RegistryEvent.Register<ParticleType<?>> event) {
        event.getRegistry().register(SoulboundArmory.criticalHitParticleType.setRegistryName("critical_hit"));
        event.getRegistry().register(SoulboundArmory.unlockParticle.setRegistryName("unlock"));
    }

    @SubscribeEvent
    public static void registerSound(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(SoulboundArmory.unlockAnimationSound.setRegistryName("unlock"));
    }
}
