package user11681.soulboundarmory.asm.mixin.entity.player;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.item.SoulboundItem;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryEntityMixin {
    @Unique
    protected final PlayerInventory self = (PlayerInventory) (Object) this;

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    public void insertStack(int slot, final ItemStack stack, final CallbackInfoReturnable<Boolean> info) {
        final Item item = stack.getItem();

        if (item instanceof SoulboundItem) {
            final ItemStorage<?> storage = StorageType.get(self.player, item);
        }
    }
}
