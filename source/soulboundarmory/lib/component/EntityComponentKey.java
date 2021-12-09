package soulboundarmory.lib.component;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import soulboundarmory.lib.component.access.EntityAccess;

/**
 A key that corresponds to a registered entity component; used for extracting components from entities.

 @param <E> the base type of the entities for which the component is registered
 @param <C> the type of the component
 */
public final class EntityComponentKey<C extends Component> extends ComponentKey<C> {
    EntityComponentKey(Class<?> type, Identifier id, Predicate<?> predicate, Function<?, C> instantiate) {
        super(type, id, predicate, instantiate);
    }

    @Override
    public C of(Object object) {
        return object instanceof EntityAccess entity ? (C) entity.soulboundarmory$components().get(this) : null;
    }
}
