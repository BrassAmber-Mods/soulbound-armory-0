package user11681.soulboundarmory.event;

import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.SoulboundArmoryClient;
import user11681.soulboundarmory.capability.CapabilityStorage;
import user11681.soulboundarmory.capability.config.ConfigCapability;
import user11681.soulboundarmory.capability.entity.EntityData;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.soulbound.player.ToolCapability;
import user11681.soulboundarmory.capability.soulbound.player.WeaponCapability;
import user11681.soulboundarmory.capability.statistics.Category;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.client.render.SoulboundDaggerEntityRenderer;
import user11681.soulboundarmory.client.render.SoulboundFireballEntityRenderer;
import user11681.soulboundarmory.entity.SAAttributes;
import user11681.soulboundarmory.entity.SoulboundDaggerEntity;
import user11681.soulboundarmory.entity.SoulboundFireballEntity;
import user11681.soulboundarmory.registry.SoulboundItems;
import user11681.soulboundarmory.skill.Skill;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = SoulboundArmory.ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ConfigCapability.class, new CapabilityStorage<>(), () -> new ConfigCapability(null));
        CapabilityManager.INSTANCE.register(ToolCapability.class, new CapabilityStorage<>(), () -> new ToolCapability(null));
        CapabilityManager.INSTANCE.register(WeaponCapability.class, new CapabilityStorage<>(), () -> new WeaponCapability(null));
        CapabilityManager.INSTANCE.register(EntityData.class, new CapabilityStorage<>(), () -> new EntityData(null));
    }

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
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(SoulboundItems.dagger.setRegistryName("dagger"));
        registry.register(SoulboundItems.sword.setRegistryName("sword"));
        registry.register(SoulboundItems.greatsword.setRegistryName("greatsword"));
        registry.register(SoulboundItems.staff.setRegistryName("staff"));
        registry.register(SoulboundItems.pick.setRegistryName("pick"));
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(SoulboundFireballEntity.type.setRegistryName("fireball"));
    }

    @SubscribeEvent
    public static void registerAttributes(RegistryEvent.Register<Attribute> event) {
        event.getRegistry().register(SAAttributes.efficiency.setRegistryName("efficiency"));
        event.getRegistry().register(SAAttributes.criticalStrikeRate.setRegistryName("critical_strike_rate"));
    }

    @SubscribeEvent
    public static void registerEnchantment(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().register(SoulboundArmory.impact.setRegistryName("impact"));
    }
}
