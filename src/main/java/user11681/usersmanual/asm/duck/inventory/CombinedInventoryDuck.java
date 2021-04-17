package user11681.usersmanual.asm.duck.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface CombinedInventoryDuck {
    List<DefaultedList<ItemStack>> getCombinedInventory();
}
