package user11681.usersmanual.asm.mixin.entity.player;

import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import user11681.usersmanual.asm.duck.inventory.CombinedInventoryDuck;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements CombinedInventoryDuck {
    @Accessor
    public abstract List<DefaultedList<ItemStack>> getCombinedInventory();
}
