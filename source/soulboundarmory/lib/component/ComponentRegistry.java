package soulboundarmory.lib.component;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.lib.component.access.EntityAccess;
import soulboundarmory.util.Util;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public final class ComponentRegistry {
    public static final Map<Identifier, ItemStackComponentKey<?>> item = new Object2ReferenceOpenHashMap<>();
    private static final Map<Identifier, ComponentKey<?>> entity = new Object2ReferenceOpenHashMap<>();

    /**
     Register a component to attach to objects of the specified type.

     @param type a reference to `O`
     @param path the path of the component under the registering mod's namespace
     @param predicate a predicate that will be invoked to determine whether the component should be attached to an object
     @param instantiate a function that instantiates a component for a given object
     @param <O> the base type of the objects to which to attach the component
     @param <C> the type of the component to register
     @return a {@linkplain ComponentKey key} for the component.
     */
    public static <O, C extends Component> ComponentKey<C> register(Class<O> type, String path, Predicate<O> predicate, Function<O, C> instantiate) {
        var id = Util.id(path);

        if (Entity.class.isAssignableFrom(type)) {
            var key = new EntityComponentKey<>(type, id, predicate, instantiate);
            entity.put(id, key);

            return key;
        }

        if (ItemStack.class.isAssignableFrom(type)) {
            var itemKey = new ItemStackComponentKey<>(type, id, predicate, instantiate);
            item.put(id, itemKey);

            return itemKey;
        }

        throw new IllegalArgumentException(type.getName());
    }

    public static <O, C extends Component> ComponentKey<C> register(Class<O> type, String path, Function<O, C> instantiate) {
        return register(type, path, null, instantiate);
    }

    public static ComponentKey<?> entity(Identifier id) {
        return entity.get(id);
    }

    @SubscribeEvent
    public static void addComponents(EntityEvent.EntityConstructing event) {
        var entity = event.getEntity();

        ComponentRegistry.entity.values().forEach(key -> {
            if (key.type.isInstance(entity)) {
                var component = key.instantiate(entity);

                if (component != null) {
                    ((EntityAccess) entity).soulboundarmory$components().put(key, component);
                }
            }
        });
    }
}
