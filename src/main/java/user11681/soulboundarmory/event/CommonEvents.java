package user11681.soulboundarmory.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.capability.CapabilityStorage;
import user11681.soulboundarmory.capability.entity.EntityData;
import user11681.soulboundarmory.capability.soulbound.player.ToolCapability;
import user11681.soulboundarmory.capability.soulbound.player.WeaponCapability;
import user11681.soulboundarmory.command.SoulboundArmoryCommand;
import user11681.soulboundarmory.registry.SoulboundItems;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public class CommonEvents {
    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {
        SoulboundItems.register();

        CapabilityManager.INSTANCE.register(WeaponCapability.class, new CapabilityStorage<>(), WeaponCapability::new);
        CapabilityManager.INSTANCE.register(ToolCapability.class, new CapabilityStorage<>(), ToolCapability::new);
        CapabilityManager.INSTANCE.register(EntityData.class, new CapabilityStorage<>(), EntityData::new);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(SoulboundArmory.id("data"), new CapabilityProvider());
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(SoulboundArmoryCommand.get());
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().register(SoulboundArmory.impact);
    }

    private static class CapabilityProvider implements ICapabilityProvider {
        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return LazyOptional.of(cap::getDefaultInstance);
        }
    }
}
