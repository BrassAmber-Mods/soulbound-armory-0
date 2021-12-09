package soulboundarmory.lib.component;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import soulboundarmory.lib.component.access.ItemStackAccess;

public class ItemStackComponentKey<C extends Component> extends ComponentKey<C> {
    ItemStackComponentKey(Class<?> type, Identifier id, Predicate<?> predicate, Function<?, C> instantiate) {
        super(type, id, predicate, instantiate);
    }

    @Override
    public C of(Object object) {
        return object instanceof ItemStackAccess stack ? (C) stack.soulboundarmory$component(this) : null;
    }
}
