package soulboundarmory.component;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.component.config.ConfigComponent;
import soulboundarmory.component.entity.EntityData;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.soulbound.player.ToolComponent;
import soulboundarmory.component.soulbound.player.WeaponComponent;

public class Components {
    public static final ComponentKey<PlayerEntity, ConfigComponent> config = ComponentRegistry.register(PlayerEntity.class, SoulboundArmory.id("config"), ConfigComponent::new);
    public static final ComponentKey<Entity, EntityData> entityData = ComponentRegistry.register(Entity.class, SoulboundArmory.id("data"), EntityData::new);
    public static final ComponentKey<PlayerEntity, ToolComponent> tool = ComponentRegistry.register(PlayerEntity.class, SoulboundArmory.id("tool"), ToolComponent::new);
    public static final ComponentKey<PlayerEntity, WeaponComponent> weapon = ComponentRegistry.register(PlayerEntity.class, SoulboundArmory.id("weapon"), WeaponComponent::new);
    // public static final CapabilityContainer<ItemData> itemData = register(ItemData.class, ItemData::new);

    public static final List<ComponentKey<PlayerEntity, ? extends SoulboundComponent>> soulboundComponents = List.of(tool, weapon);

    public static Stream<SoulboundComponent> soulbound(Entity entity) {
        return soulboundComponents.stream().map(key -> key.of(entity));
    }
}
