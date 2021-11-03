package user11681.soulboundarmory.capability;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
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
    public static final CapabilityContainer<ConfigCapability> config = container(ConfigCapability.class);
    public static final CapabilityContainer<EntityData> entityData = container(EntityData.class);
    public static final CapabilityContainer<ToolCapability> tool = container(ToolCapability.class);
    public static final CapabilityContainer<WeaponCapability> weapon = container(WeaponCapability.class);
    // public static final CapabilityContainer<ItemData> itemData = register(ItemData.class, ItemData::new);

    public static final List<CapabilityContainer<? extends SoulboundCapability>> soulboundCapabilities = Arrays.asList(tool, weapon);

    public static Stream<SoulboundCapability> get(Entity entity) {
        return soulboundCapabilities.stream().map(type -> type.get(entity));
    }

    private static <T extends CompoundSerializable> CapabilityContainer<T> container(Class<T> type) {
        return new CapabilityContainer<>(Accessor.<Map<String, Capability<T>>>getObject(CapabilityManager.INSTANCE, "providers").get(type.getName()));
    }
}
