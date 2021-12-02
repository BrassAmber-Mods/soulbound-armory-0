package soulboundarmory.mixin.mixin.entity.player;

import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerInventory.class)
public interface PlayerInventoryAccess {
    @Accessor("compartments")
    List<DefaultedList<ItemStack>> compartments();
}
