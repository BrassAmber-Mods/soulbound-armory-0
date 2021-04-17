package user11681.soulboundarmory.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import java.util.ArrayList;
import java.util.List;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import nerdhub.cardinal.components.api.event.ItemComponentCallback;
import nerdhub.cardinal.components.api.util.EntityComponents;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.component.config.ConfigComponent;
import user11681.soulboundarmory.component.entity.EntityData;
import user11681.soulboundarmory.component.soulbound.item.ItemData;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.soulbound.player.ToolSoulboundComponent;
import user11681.soulboundarmory.component.soulbound.player.WeaponSoulboundComponent;

public class Components {
    public static final List<ComponentKey<? extends SoulboundComponent<?>>> soulboundComponents = new ArrayList<>();

    public static final ComponentKey<ConfigComponent> config = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(SoulboundArmory.ID, "config_component"), ConfigComponent.class);
    public static final ComponentKey<EntityData> entityData = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(SoulboundArmory.ID, "entity_data"), EntityData.class);
    public static final ComponentKey<ItemData> itemData = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(SoulboundArmory.ID, "item_data"), ItemData.class);
    public static final ComponentKey<ToolSoulboundComponent> toolComponent = registerSoulbound(new Identifier(SoulboundArmory.ID, "tool"), ToolSoulboundComponent.class);
    public static final ComponentKey<WeaponSoulboundComponent> weaponComponent = registerSoulbound(new Identifier(SoulboundArmory.ID, "weapon"), WeaponSoulboundComponent.class);

    public static <T extends SoulboundComponent<T>> ComponentKey<T> registerSoulbound(Identifier identifier, Class<T> clazz) {
        ComponentKey<T> type = ComponentRegistryV3.INSTANCE.getOrCreate(identifier, clazz);

        soulboundComponents.add(type);

        return type;
    }

    public static List<SoulboundComponent<?>> getComponents(Entity entity) {
        List<SoulboundComponent<?>> components = new ArrayList<>();

        for (ComponentKey<? extends SoulboundComponent<?>> type : soulboundComponents) {
            components.add(type.get(entity));
        }

        return components;
    }

    public static void setup() {
        setRespawnCopyStrategy(Components.config, RespawnCopyStrategy.ALWAYS_COPY);
        setRespawnCopyStrategy(Components.toolComponent, RespawnCopyStrategy.ALWAYS_COPY);
        setRespawnCopyStrategy(Components.weaponComponent, RespawnCopyStrategy.ALWAYS_COPY);

        config.attach(EntityComponentCallback.event(PlayerEntity.class), ConfigComponent::new);
        entityData.attach(EntityComponentCallback.event(Entity.class), EntityData::new);
        toolComponent.attach(EntityComponentCallback.event(PlayerEntity.class), ToolSoulboundComponent::new);
        weaponComponent.attach(EntityComponentCallback.event(PlayerEntity.class), WeaponSoulboundComponent::new);

        for (Item item : Registry.ITEM) {
            itemData.attach(ItemComponentCallback.event(item), ItemData::new);
        }
    }
}
