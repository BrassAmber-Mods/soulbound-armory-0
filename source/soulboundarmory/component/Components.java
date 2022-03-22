package soulboundarmory.component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import soulboundarmory.component.config.ConfigComponent;
import soulboundarmory.component.entity.DaggerMarkerComponent;
import soulboundarmory.component.entity.EntityData;
import soulboundarmory.component.soulbound.item.ItemMarkerComponent;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.component.soulbound.player.MasterToolComponent;
import soulboundarmory.component.soulbound.player.MasterWeaponComponent;
import soulboundarmory.item.SoulboundItem;
import soulboundarmory.item.SoulboundItems;
import soulboundarmory.module.component.ComponentRegistry;
import soulboundarmory.module.component.EntityComponentKey;
import soulboundarmory.module.component.ItemStackComponentKey;

public final class Components {
    public static final EntityComponentKey<ConfigComponent> config = ComponentRegistry.entity(PlayerEntity.class, "config", ConfigComponent::new);
    public static final EntityComponentKey<EntityData> entityData = ComponentRegistry.entity(Entity.class, "data", EntityData::new);
    public static final EntityComponentKey<MasterToolComponent> tool = ComponentRegistry.entity(PlayerEntity.class, "tool", player -> !(player.world.isClient && player instanceof OtherClientPlayerEntity), MasterToolComponent::new);
    public static final EntityComponentKey<MasterWeaponComponent> weapon = ComponentRegistry.entity(PlayerEntity.class, "weapon", player -> !(player.world.isClient && player instanceof OtherClientPlayerEntity), MasterWeaponComponent::new);
    public static final ItemStackComponentKey<ItemMarkerComponent> marker = ComponentRegistry.item("marker", stack -> stack.getItem() instanceof SoulboundItem, ItemMarkerComponent::new);
    public static final ItemStackComponentKey<DaggerMarkerComponent> dagger = ComponentRegistry.item("dagger", stack -> stack.isOf(SoulboundItems.dagger), DaggerMarkerComponent::new);

    private static final List<EntityComponentKey<? extends MasterComponent<?>>> soulboundComponents = List.of(tool, weapon);

    public static Stream<? extends MasterComponent<?>> soulbound(Entity entity) {
        return soulboundComponents.stream().map(key -> key.of(entity)).filter(Objects::nonNull);
    }
}
