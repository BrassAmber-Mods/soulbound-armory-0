package soulboundarmory.component;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public final class ComponentRegistry {
    private static final Map<Identifier, ComponentKey<?, ?>> registry = new Object2ReferenceOpenHashMap<>();

    public static <E extends Entity, C extends Component> ComponentKey<E, C> register(Class<E> type, Identifier id, Function<E, C> instantiate) {
        var key = new ComponentKey<>(type, id, instantiate);
        registry.put(id, key);

        return key;
    }

    public static ComponentKey<?, ?> get(Identifier id) {
        return registry.get(id);
    }

    public static void each(Consumer<ComponentKey<?, ?>> action) {
        registry.values().forEach(action);
    }
}
