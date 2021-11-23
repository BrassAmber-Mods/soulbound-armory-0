package net.auoeke.soulboundarmory.asm.mixin.entity.player;

import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerInventory.class)
public interface PlayerInventoryAccess {
    @Accessor("combinedInventory")
    List<NonNullList<ItemStack>> compartments();
}
