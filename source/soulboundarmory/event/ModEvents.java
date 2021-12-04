package soulboundarmory.event;

import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.SoulboundArmoryClient;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.client.render.SoulboundDaggerEntityRenderer;
import soulboundarmory.client.render.SoulboundFireballEntityRenderer;
import soulboundarmory.entity.SAAttributes;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.entity.SoulboundFireballEntity;
import soulboundarmory.registry.SoulboundItems;
import soulboundarmory.skill.Skill;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SoulboundArmory.ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(SoulboundArmoryClient.guiKeyBinding);
        ClientRegistry.registerKeyBinding(SoulboundArmoryClient.toggleXPBarKeyBinding);

        RenderingRegistry.registerEntityRenderingHandler(SoulboundDaggerEntity.type, SoulboundDaggerEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SoulboundFireballEntity.type, SoulboundFireballEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerRegistries(RegistryEvent.NewRegistry event) {
        Unsafe.ensureClassInitialized(StorageType.class);
        Unsafe.ensureClassInitialized(Category.class);
        Unsafe.ensureClassInitialized(StatisticType.class);
        Unsafe.ensureClassInitialized(Skill.class);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        var registry = event.getRegistry();
        registry.register(SoulboundItems.dagger.setRegistryName("dagger"));
        registry.register(SoulboundItems.sword.setRegistryName("sword"));
        registry.register(SoulboundItems.greatsword.setRegistryName("greatsword"));
        registry.register(SoulboundItems.staff.setRegistryName("staff"));
        registry.register(SoulboundItems.pick.setRegistryName("pick"));
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(SoulboundFireballEntity.type.setRegistryName("fireball"));

        Unsafe.ensureClassInitialized(Components.class);
    }

    @SubscribeEvent
    public static void registerAttributes(RegistryEvent.Register<EntityAttribute> event) {
        event.getRegistry().register(SAAttributes.efficiency.setRegistryName("efficiency"));
        event.getRegistry().register(SAAttributes.criticalStrikeRate.setRegistryName("critical_strike_rate"));
    }

    @SubscribeEvent
    public static void registerEnchantment(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().register(SoulboundArmory.impact.setRegistryName("impact"));
    }
}
