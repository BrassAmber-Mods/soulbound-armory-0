package user11681.soulboundarmory.capability;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import user11681.reflect.Accessor;
import user11681.soulboundarmory.capability.config.ConfigCapability;
import user11681.soulboundarmory.capability.entity.EntityData;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.capability.soulbound.player.ToolCapability;
import user11681.soulboundarmory.capability.soulbound.player.WeaponCapability;
import user11681.soulboundarmory.serial.CompoundSerializable;

public class Capabilities {
    public static final List<CapabilityContainer<? extends SoulboundCapability>> soulboundCapabilities = new ArrayList<>(2);

    public static final CapabilityContainer<ConfigCapability> config = register(ConfigCapability.class, ConfigCapability::new);
    public static final CapabilityContainer<EntityData> entityData = register(EntityData.class, EntityData::new);
    public static final CapabilityContainer<ToolCapability> tool = registerSoulbound(ToolCapability.class, ToolCapability::new);
    public static final CapabilityContainer<WeaponCapability> weapon = registerSoulbound(WeaponCapability.class, WeaponCapability::new);
    //    public static final CapabilityContainer<ItemData> itemData = register(ItemData.class, ItemData::new);

    public static <T extends SoulboundCapability> CapabilityContainer<T> registerSoulbound(Class<T> klass, Callable<T> factory) {
        CapabilityContainer<T> type = register(klass, factory);

        soulboundCapabilities.add(type);

        return type;
    }

    public static List<SoulboundCapability> get(Entity entity) {
        List<SoulboundCapability> components = new ArrayList<>();

        for (CapabilityContainer<? extends SoulboundCapability> type : soulboundCapabilities) {
            components.add(type.get(entity));
        }

        return components;
    }

    public static <T extends CompoundSerializable> CapabilityContainer<T> register(Class<T> type, Callable<T> factory) {
        CapabilityManager.INSTANCE.register(type, new CapabilityStorage<>(), factory);

        return new CapabilityContainer<>(Accessor.<IdentityHashMap<String, Capability<T>>>getObject(CapabilityManager.INSTANCE, "providers").get(type.getName()));
    }
}
