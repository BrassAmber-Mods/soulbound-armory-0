package soulboundarmory.module.component;

import java.lang.ref.WeakReference;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import soulboundarmory.module.component.access.ItemStackAccess;
import soulboundarmory.util.Util;
import soulboundarmory.util.Util2;

public final class ItemStackComponentKey<C extends ItemStackComponent<C>> extends ComponentKey<ItemStack, C> {
	ItemStackComponentKey(Identifier id, Predicate<ItemStack> predicate, Function<ItemStack, C> instantiate) {
		super(id, predicate, instantiate);
	}

	@Override
	public C attach(ItemStack stack) {
		if (stack != null && (this.predicate == null || this.predicate.test(Util2.cast(stack)))) {
			var component = this.instantiate.apply(Util2.cast(stack));
			var previous = ((ItemStackAccess) (Object) stack).soulboundarmory$component(this, component);

			if (previous != null) {
				throw new IllegalArgumentException("component %s is already attached".formatted(this.key));
			}

			(Util.isClient() ? ComponentRegistry.tickingClient : ComponentRegistry.tickingServer).add(new WeakReference<>(component));

			return component;
		}

		return null;
	}

	@Override
	public C of(ItemStack stack) {
		return stack == null ? null : (C) ((ItemStackAccess) (Object) stack).soulboundarmory$component(this);
	}
}
