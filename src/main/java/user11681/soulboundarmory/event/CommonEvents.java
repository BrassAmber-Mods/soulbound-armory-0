package user11681.soulboundarmory.event;

import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.capability.Capabilities;
import user11681.soulboundarmory.capability.config.ConfigCapability;
import user11681.soulboundarmory.capability.entity.EntityData;
import user11681.soulboundarmory.capability.soulbound.player.ToolCapability;
import user11681.soulboundarmory.capability.soulbound.player.WeaponCapability;
import user11681.soulboundarmory.command.SoulboundArmoryCommand;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public class CommonEvents {
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();

        if (entity instanceof PlayerEntity player) {
            event.addCapability(SoulboundArmory.id("config"), new EntityProvider<>(player, Capabilities.config.capability, ConfigCapability::new));
            event.addCapability(SoulboundArmory.id("tool"), new EntityProvider<>(player, Capabilities.tool.capability, ToolCapability::new));
            event.addCapability(SoulboundArmory.id("weapon"), new EntityProvider<>(player, Capabilities.weapon.capability, WeaponCapability::new));
        }

        if (entity instanceof LivingEntity || entity instanceof ProjectileEntity) {
            event.addCapability(SoulboundArmory.id("data"), new EntityProvider<>(entity, Capabilities.entityData.capability, EntityData::new));
        }
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(SoulboundArmoryCommand.get());
    }

    private static class EntityProvider<E extends Entity> implements ICapabilityProvider {
        private final E entity;
        private final Function<E, ?> factory;
        private final Capability<?> capability;

        public EntityProvider(E entity, Capability<?> capability, Function<E, ?> factory) {
            this.entity = entity;
            this.factory = factory;
            this.capability = capability;
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == this.capability ? LazyOptional.of(() -> (T) this.factory.apply(this.entity)) : LazyOptional.empty();
        }
    }
}
