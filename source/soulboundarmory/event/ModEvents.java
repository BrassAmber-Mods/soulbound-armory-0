package soulboundarmory.event;

import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.NewRegistryEvent;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.command.argument.ItemComponentArgumentType;
import soulboundarmory.command.argument.RegistryArgumentType;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponentType;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.entity.SoulboundDaggerEntity;
import soulboundarmory.item.SoulboundItems;
import soulboundarmory.skill.Skills;
import soulboundarmory.util.Util;

@EventBusSubscriber(modid = SoulboundArmory.ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModEvents {
    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        ArgumentTypes.register(Util.id("item_component_registry").toString(), ItemComponentArgumentType.class, new ConstantArgumentSerializer<>(ItemComponentArgumentType::itemComponents));
        ArgumentTypes.register(Util.id("registry").toString(), Util.cast(RegistryArgumentType.class), new RegistryArgumentType.Serializer());
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
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        event.getRegistry().register(SoulboundArmory.criticalHitParticleType.setRegistryName("critical_hit"));
        event.getRegistry().register(SoulboundArmory.unlockParticle.setRegistryName("unlock"));
    }

    @SubscribeEvent
    public static void registerSound(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(SoulboundArmory.unlockAnimationSound.setRegistryName("unlock"));
    }
}
