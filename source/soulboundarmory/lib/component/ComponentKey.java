package soulboundarmory.lib.component;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;

/**
 A key that corresponds to a registered component; used for extracting components from objects.

 @param <C> the type of the component
 */
public abstract class ComponentKey<B, C extends Component<C>> {
    public final Identifier id;
    public final String key;
    public final Predicate<?> predicate;

    protected final Function<?, C> instantiate;

    <O extends B> ComponentKey(Identifier id, Predicate<O> predicate, Function<O, C> instantiate) {
        this.id = id;
        this.key = id.toString();
        this.predicate = predicate;
        this.instantiate = instantiate;
    }

    /**
     Attach a new instance of the component if the supplied object is of the component's target type.

     @param object the object to test
     @return the new instance of the component if it is applicable to the object or null
     @throws IllegalArgumentException if the component is already attached to the object.
     */
    public abstract C attach(B object);

    /**
     Extract the instance of this component from an object.

     @param object the object wherefrom to extract
     @return null if `object` is null; otherwise the component instance attached to `object` or null.
     */
    public abstract C of(B object);

    /**
     Extract the instance of this component from an object and wrap it in an {@link Optional}.

     @param object the object wherefrom to extract
     @return empty if `object` is null; otherwise an {@link Optional} of the component instance attached to `object` or empty.
     */
    public final Optional<C> nullable(B object) {
        return Optional.ofNullable(this.of(object));
    }

    /**
     Extract the instance of this component from an object if it is attached or attach a new component.

     @param object the object wherefrom to extract or whereto to attach
     @return the component instance.
     */
    public final C obtain(B object) {
        return this.nullable(object).orElseGet(() -> this.attach(object));
    }
}
