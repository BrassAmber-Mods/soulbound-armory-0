package soulboundarmory.lib.component;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.lib.component.access.EntityAccess;
import soulboundarmory.util.Util;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public final class ComponentRegistry {
    public static final Map<Identifier, ItemStackComponentKey<?>> item = new Object2ReferenceOpenHashMap<>();
    private static final Map<Identifier, EntityComponentKey<?>> entity = new Object2ReferenceOpenHashMap<>();

    /**
     Register a component to attach to entities of a specified base type.

     @param type        a reference to `E`
     @param path        the path of the component under the registering mod's namespace
     @param predicate   a predicate that will be invoked to determine whether the component should be attached to an entity
     @param instantiate a function that instantiates the component for a given object
     @param <E>         the base type of the entities to which to attach the component
     @param <C>         the type of the component to register
     @return a {@linkplain EntityComponentKey key} for the component.
     */
    public static <E extends Entity, C extends EntityComponent<C>> EntityComponentKey<C> entity(Class<E> type, String path, Predicate<E> predicate, Function<E, C> instantiate) {
        var key = new EntityComponentKey<>(type, Util.id(path), predicate, instantiate);
        entity.put(key.id, key);

        return key;
    }

    /**
     Register a component to attach to all entities of a specified base type.

     @param <E>         the base type of the entities to which to attach the component
     @param <C>         the type of the component to register
     @param type        a reference to `E`
     @param path        the path of the component under the registering mod's namespace
     @param instantiate a function that instantiates the component for a given object
     @return a {@linkplain EntityComponentKey key} for the component.
     */
    public static <E extends Entity, C extends EntityComponent<C>> EntityComponentKey<C> entity(Class<E> type, String path, Function<E, C> instantiate) {
        return entity(type, path, null, instantiate);
    }

    /**
     Register a component to attach to item stacks.

     @param path        the path of the component under the registering mod's namespace
     @param predicate   a predicate that will be invoked to determine whether the component should be attached to an item stack
     @param instantiate a function that instantiates a component for a given item stack
     @param <C>         the type of the component to register
     @return a {@linkplain ItemStackComponentKey key} for the component.
     */
    public static <C extends ItemStackComponent<C>> ItemStackComponentKey<C> item(String path, Predicate<ItemStack> predicate, Function<ItemStack, C> instantiate) {
        var key = new ItemStackComponentKey<>(Util.id(path), predicate, instantiate);
        item.put(key.id, key);

        return key;
    }

    /**
     Register a component to attach to all item stacks.

     @param path        the path of the component under the registering mod's namespace
     @param instantiate a function that instantiates a component for a given item stack
     @param <C>         the type of the component to register
     @return a {@linkplain ItemStackComponentKey key} for the component.
     */
    public static <C extends ItemStackComponent<C>> ItemStackComponentKey<C> item(String path, Function<ItemStack, C> instantiate) {
        return item(path, null, instantiate);
    }

    /**
     Find the entity component key with a given identifier.

     @param id the component's identifier
     @return the component key if it exists or null.
     */
    public static EntityComponentKey<?> findEntity(Identifier id) {
        return entity.get(id);
    }

    @SubscribeEvent
    public static void addComponents(EntityEvent.EntityConstructing event) {
        entity.values().forEach(key -> key.attach(event.getEntity()));
    }

    @SubscribeEvent
    public static void spawn(EntityJoinWorldEvent event) {
        ((EntityAccess) event.getEntity()).soulboundarmory$components().values().forEach(EntityComponent::spawn);
    }

    @SubscribeEvent
    public static void onPlayerCopy(PlayerEvent.Clone event) {
        for (var key : entity.values()) {
            var original = key.of(event.getOriginal());
            var copy = key.of(event.getPlayer());

            if (original != null && copy != null) {
                original.copy(Util.cast(copy));
            }
        }
    }
}
