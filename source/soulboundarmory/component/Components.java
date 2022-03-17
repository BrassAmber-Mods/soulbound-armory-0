package soulboundarmory.component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import soulboundarmory.component.config.ConfigComponent;
import soulboundarmory.component.entity.DaggerItemComponent;
import soulboundarmory.component.entity.EntityData;
import soulboundarmory.component.soulbound.item.ItemMarkerComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.soulbound.player.SoulboundToolComponent;
import soulboundarmory.component.soulbound.player.SoulboundWeaponComponent;
import soulboundarmory.item.SoulboundItem;
import soulboundarmory.lib.component.ComponentRegistry;
import soulboundarmory.lib.component.EntityComponentKey;
import soulboundarmory.lib.component.ItemStackComponentKey;
import soulboundarmory.item.SoulboundItems;

public final class Components {
    public static final EntityComponentKey<ConfigComponent> config = ComponentRegistry.entity(PlayerEntity.class, "config", ConfigComponent::new);
    public static final EntityComponentKey<EntityData> entityData = ComponentRegistry.entity(Entity.class, "data", EntityData::new);
    public static final EntityComponentKey<SoulboundToolComponent> tool = ComponentRegistry.entity(PlayerEntity.class, "tool", player -> !(player.world.isClient && player instanceof OtherClientPlayerEntity), SoulboundToolComponent::new);
    public static final EntityComponentKey<SoulboundWeaponComponent> weapon = ComponentRegistry.entity(PlayerEntity.class, "weapon", player -> !(player.world.isClient && player instanceof OtherClientPlayerEntity), SoulboundWeaponComponent::new);
    public static final ItemStackComponentKey<ItemMarkerComponent> marker = ComponentRegistry.item("marker", stack -> stack.getItem() instanceof SoulboundItem, ItemMarkerComponent::new);
    public static final ItemStackComponentKey<DaggerItemComponent> dagger = ComponentRegistry.item("dagger", stack -> stack.isOf(SoulboundItems.dagger), DaggerItemComponent::new);

    private static final List<EntityComponentKey<? extends SoulboundComponent<?>>> soulboundComponents = List.of(tool, weapon);

    public static Stream<? extends SoulboundComponent<?>> soulbound(Entity entity) {
        return soulboundComponents.stream().map(key -> key.of(entity)).filter(Objects::nonNull);
    }
}
