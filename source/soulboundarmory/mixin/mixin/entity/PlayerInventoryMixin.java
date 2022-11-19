package soulboundarmory.mixin.mixin.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.player.MasterComponent;

@Mixin(PlayerInventory.class)
abstract class PlayerInventoryMixin {
	@Shadow @Final public PlayerEntity player;

	@Shadow public abstract boolean insertStack(int slot, ItemStack stack);

	/**
	 Ensure that items bound to certain slots are inserted thereinto.
	 */
	@Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
	public void insertIntoBoundSlot(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		MasterComponent.of(this.player, stack).ifPresent(component -> {
			if (component.hasBoundSlot() && component.stackInBoundSlot().isEmpty()) {
				info.setReturnValue(this.insertStack(component.boundSlot(), stack));
			}
		});
	}

	/**
	 Prevent items from being inserted into bound slots.
	 */
	@Redirect(method = "getEmptySlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;"))
	private Object reserveBoundSlot(DefaultedList<ItemStack> inventory, int index) {
		var stack = inventory.get(index);
		return Components.soulbound(this.player).allMatch(component -> component.boundSlot() != index || component.accepts(stack)) ? stack : new ItemStack(Items.STONE, Integer.MAX_VALUE);
	}
}
