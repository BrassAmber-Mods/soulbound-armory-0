package soulboundarmory.mixin.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import soulboundarmory.component.Components;

@Mixin(MiningToolItem.class)
abstract class MiningToolItemMixin {
	@Redirect(method = "isCorrectToolForDrops", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/MiningToolItem;getMaterial()Lnet/minecraft/item/ToolMaterial;"))
	private ToolMaterial checkSoulboundItemHasSuitableMiningLevel(MiningToolItem item, ItemStack stack) {
		return Components.marker.optional(stack).map(marker -> marker.item().material()).orElse(item.getMaterial());
	}
}
