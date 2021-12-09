package soulboundarmory.event;

import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.SoulboundArmoryClient;
import soulboundarmory.client.render.SoulboundDaggerEntityRenderer;
import soulboundarmory.client.render.SoulboundFireballEntityRenderer;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.entity.SoulboundFireballEntity;
import soulboundarmory.registry.Skills;
import soulboundarmory.registry.SoulboundItems;

@EventBusSubscriber(modid = SoulboundArmory.ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEvents {
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(SoulboundArmoryClient.guiKeyBinding);
        // ClientRegistry.registerKeyBinding(SoulboundArmoryClient.toggleXPBarKeyBinding);

        RenderingRegistry.registerEntityRenderingHandler(SoulboundDaggerEntity.type, SoulboundDaggerEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SoulboundFireballEntity.type, SoulboundFireballEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerRegistries(RegistryEvent.NewRegistry event) {
        Unsafe.ensureClassInitialized(ItemComponentType.class);
        Unsafe.ensureClassInitialized(Category.class);
        Unsafe.ensureClassInitialized(StatisticType.class);
        Unsafe.ensureClassInitialized(Skills.class);
        Unsafe.ensureClassInitialized(Components.class);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(SoulboundItems.dagger, SoulboundItems.sword, SoulboundItems.greatsword, SoulboundItems.staff, SoulboundItems.pick);
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(SoulboundFireballEntity.type.setRegistryName("fireball"));
    }

    @SubscribeEvent
    public static void registerEnchantment(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().register(SoulboundArmory.impact.setRegistryName("impact"));
    }
}
