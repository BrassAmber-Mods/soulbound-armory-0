package soulboundarmory.component;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ComponentRegistry {
    public static final List<ComponentKey<?, ?>> registry = new ReferenceArrayList<>();

    public static <E extends Entity, C extends Component> ComponentKey<E, C> register(Class<E> type, ResourceLocation id, Function<E, C> instantiate) {
        var key = new ComponentKey<>(type, id, instantiate);
        registry.add(key);

        return key;
    }
}
