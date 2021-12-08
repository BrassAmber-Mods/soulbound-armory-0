package soulboundarmory.component;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.mixin.access.entity.EntityAccess;

@EventBusSubscriber(modid = SoulboundArmory.ID)
public final class ComponentRegistry {
    private static final Map<Identifier, ComponentKey<?, ?>> registry = new Object2ReferenceOpenHashMap<>();

    /**
     Register a component to attach to entities of the specified type.

     @param type a reference to `E`
     @param id the identifier of the component
     @param instantiate a function that instantiates a component for a given entity
     @param <E> the base type of the entities to which to attach the component
     @param <C> the type of the component
     @return a {@linkplain ComponentKey key} for the component.
     */
    public static <E extends Entity, C extends Component> ComponentKey<E, C> register(Class<E> type, Identifier id, Function<E, C> instantiate) {
        var key = new ComponentKey<>(type, id, instantiate);
        registry.put(id, key);

        return key;
    }

    public static ComponentKey<?, ?> get(Identifier id) {
        return registry.get(id);
    }

    @SubscribeEvent
    public static void construct(EntityEvent.EntityConstructing event) {
        var entity = event.getEntity();

        registry.values().forEach(key -> {
            if (key.type.isInstance(entity)) {
                ((EntityAccess) entity).soulboundarmory$components().put(key, ((Function<Entity, Component>) key.instantiate).apply(entity));
            }
        });
    }
}
