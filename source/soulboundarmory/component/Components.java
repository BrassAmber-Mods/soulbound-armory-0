package soulboundarmory.component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import soulboundarmory.component.config.ConfigComponent;
import soulboundarmory.component.entity.EntityData;
import soulboundarmory.component.soulbound.item.ItemMarkerComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.soulbound.player.ToolComponent;
import soulboundarmory.component.soulbound.player.WeaponComponent;
import soulboundarmory.item.SoulboundItem;
import soulboundarmory.lib.component.ComponentRegistry;
import soulboundarmory.lib.component.EntityComponentKey;
import soulboundarmory.lib.component.ItemStackComponentKey;

public final class Components {
    public static final EntityComponentKey<ConfigComponent> config = ComponentRegistry.entity(PlayerEntity.class, "config", ConfigComponent::new);
    public static final EntityComponentKey<EntityData> entityData = ComponentRegistry.entity(Entity.class, "data", EntityData::new);
    public static final EntityComponentKey<ToolComponent> tool = ComponentRegistry.entity(PlayerEntity.class, "tool", ToolComponent::new);
    public static final EntityComponentKey<WeaponComponent> weapon = ComponentRegistry.entity(PlayerEntity.class, "weapon", WeaponComponent::new);
    public static final ItemStackComponentKey<ItemMarkerComponent> marker = ComponentRegistry.item("marker", stack -> stack.getItem() instanceof SoulboundItem, ItemMarkerComponent::new);

    public static final List<EntityComponentKey<? extends SoulboundComponent<?>>> soulboundComponents = List.of(tool, weapon);

    public static Stream<? extends SoulboundComponent<?>> soulbound(Entity entity) {
        return soulboundComponents.stream().map(key -> key.of(entity)).filter(Objects::nonNull);
    }
}
