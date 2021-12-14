package soulboundarmory.lib.component;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import soulboundarmory.lib.component.access.EntityAccess;
import soulboundarmory.util.Util;

/**
 A key that corresponds to a registered entity component; used for extracting components from entities.

 @param <E> the base type of the entities for which the component is registered
 @param <C> the type of the component
 */
public final class EntityComponentKey<C extends EntityComponent<C>> extends ComponentKey<Entity, C> {
    public final Class<? extends Entity> type;

    <O extends Entity> EntityComponentKey(Class<O> type, Identifier id, Predicate<O> predicate, Function<O, C> instantiate) {
        super(id, predicate, instantiate);

        this.type = type;
    }

    @Override
    public C attach(Entity entity) {
        if (this.type.isInstance(entity)) {
            var component = this.instantiate.apply(Util.cast(entity));
            var previous = ((EntityAccess) entity).soulboundarmory$components().put(this, component);

            if (previous != null) {
                throw new IllegalArgumentException("component %s is already attached".formatted(this.key));
            }

            return component;
        }

        return null;
    }

    @Override
    public C of(Entity entity) {
        return entity == null ? null : (C) ((EntityAccess) entity).soulboundarmory$components().get(this);
    }
}
