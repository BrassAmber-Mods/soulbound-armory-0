package soulboundarmory.mixin.mixin.entity.player;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.component.soulbound.item.ItemComponent;

@Mixin(PlayerInventory.class)
abstract class PlayerInventoryMixin {
    /**
     Ensure that items bound to certain slots are inserted thereinto.
     */
    @Unique
    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void insertIntoBoundSlot(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        var $ = (PlayerInventory) (Object) this;

        ItemComponent.get($.player, stack).ifPresent(component -> {
            if (component.hasBoundSlot() && component.stackInBoundSlot().isEmpty()) {
                info.setReturnValue($.insertStack(component.boundSlot(), stack));
            }
        });
    }
}
