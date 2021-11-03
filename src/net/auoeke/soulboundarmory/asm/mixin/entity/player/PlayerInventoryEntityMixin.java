package net.auoeke.soulboundarmory.asm.mixin.entity.player;

import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerInventory.class)
abstract class PlayerInventoryEntityMixin {
    @Unique
    protected final PlayerInventory self = (PlayerInventory) (Object) this;

    /*
    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    public void insertStack(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> info) {
         Item item = stack.getItem();

        if (item instanceof SoulboundItem) {
             ItemStorage<?> storage = StorageType.get(self.player, item);
        }
    }
    */
}
