package soulboundarmory.lib.component;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import soulboundarmory.util.Util;

/**
 A key that corresponds to a registered component; used for extracting components from objects.

 @param <O> the base type of the entities for which the component is registered
 @param <C> the type of the component
 */
public abstract class ComponentKey<C extends Component> {
    public final Class<?> type;
    public final Identifier id;
    public final String key;

    private final Predicate<?> predicate;
    private final Function<?, C> instantiate;

    ComponentKey(Class<?> type, Identifier id, Predicate<?> predicate, Function<?, C> instantiate) {
        this.type = type;
        this.id = id;
        this.key = id.toString();
        this.predicate = predicate;
        this.instantiate = instantiate;
    }

    /**
     Instantiate a component if the supplied object matches this key's predicate. The object's type is not checked.

     @param object the object to test
     @return the component.
     */
    public C instantiate(Object object) {
        return this.predicate == null || this.predicate.test(Util.cast(object)) ? this.instantiate.apply(Util.cast(object)) : null;
    }

    /**
     @return the component attached to `object` or null.
     */
    public abstract C of(Object object);

    /**
     @return the component attached to `object`.
     */
    public final Optional<C> nullable(Object object) {
        return Optional.ofNullable(this.of(object));
    }
}
