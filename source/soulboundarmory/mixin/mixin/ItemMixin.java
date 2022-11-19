package soulboundarmory.mixin.mixin;

import java.util.Optional;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.component.Components;

@Mixin(Item.class)
abstract class ItemMixin {
	@Inject(method = "getTooltipData", at = @At("RETURN"), cancellable = true)
	private void getSoulboundItemTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<? extends TooltipData>> info) {
		if (info.getReturnValue().isEmpty()) {
			info.setReturnValue(Components.marker.optional(stack));
		}
	}
}
