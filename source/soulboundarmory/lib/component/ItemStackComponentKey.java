package soulboundarmory.lib.component;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import soulboundarmory.lib.component.access.ItemStackAccess;
import soulboundarmory.util.Util;

public final class ItemStackComponentKey<C extends ItemStackComponent<C>> extends ComponentKey<ItemStack, C> {
    ItemStackComponentKey(Identifier id, Predicate<ItemStack> predicate, Function<ItemStack, C> instantiate) {
        super(id, predicate, instantiate);
    }

    @Override
    public C attach(ItemStack stack) {
        if (this.predicate == null || this.predicate.test(Util.cast(stack))) {
            var component = this.instantiate.apply(Util.cast(stack));
            var previous = ((ItemStackAccess) (Object) stack).soulboundarmory$component(this, component);

            if (previous != null) {
                throw new IllegalArgumentException("component %s is already attached".formatted(this.key));
            }

            return component;
        }

        return null;
    }

    @Override
    public C of(ItemStack stack) {
        return stack == null ? null : (C) ((ItemStackAccess) (Object) stack).soulboundarmory$component(this);
    }
}
