package soulboundarmory.component;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import soulboundarmory.mixin.access.entity.EntityAccess;

/**
 A key that corresponds to a registered component; used for extracting components from entities.

 @param <E> the base type of the entities for which the component is registered
 @param <C> the type of the component
 */
public final class ComponentKey<E extends Entity, C extends Component> {
    public final Class<E> type;
    public final Identifier id;
    public final Function<E, C> instantiate;

    ComponentKey(Class<E> type, Identifier id, Function<E, C> instantiate) {
        this.type = type;
        this.id = id;
        this.instantiate = instantiate;
    }

    public C of(Entity entity) {
        return (C) ((EntityAccess) entity).soulboundarmory$components().get(this);
    }

    public Optional<C> nullable(Entity entity) {
        return Optional.ofNullable(this.of(entity));
    }
}
