package soulboundarmory.component;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import soulboundarmory.component.config.ConfigComponent;
import soulboundarmory.component.entity.EntityData;
import soulboundarmory.component.soulbound.item.ItemMarkerComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.soulbound.player.ToolComponent;
import soulboundarmory.component.soulbound.player.WeaponComponent;
import soulboundarmory.item.SoulboundItem;
import soulboundarmory.lib.component.ComponentKey;
import soulboundarmory.lib.component.ComponentRegistry;

public final class Components {
    public static final ComponentKey<ConfigComponent> config = ComponentRegistry.register(PlayerEntity.class, "config", ConfigComponent::new);
    public static final ComponentKey<EntityData> entityData = ComponentRegistry.register(Entity.class, "data", EntityData::new);
    public static final ComponentKey<ToolComponent> tool = ComponentRegistry.register(PlayerEntity.class, "tool", ToolComponent::new);
    public static final ComponentKey<WeaponComponent> weapon = ComponentRegistry.register(PlayerEntity.class, "weapon", WeaponComponent::new);
    public static final ComponentKey<ItemMarkerComponent> marker = ComponentRegistry.register(ItemStack.class, "marker", stack -> stack.getItem() instanceof SoulboundItem, ItemMarkerComponent::new);

    public static final List<ComponentKey<? extends SoulboundComponent>> soulboundComponents = List.of(tool, weapon);

    public static Stream<SoulboundComponent> soulbound(Entity entity) {
        return soulboundComponents.stream().map(key -> key.of(entity));
    }
}
