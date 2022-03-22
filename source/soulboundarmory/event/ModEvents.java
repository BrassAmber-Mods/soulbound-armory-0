package soulboundarmory.event;

import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
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
}
