package soulboundarmory.mixin.mixin.item;

import com.google.common.collect.Multimap;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.item.SoulboundItem;
import soulboundarmory.util.AttributeModifierIdentifiers;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {
    private AttributeModifier modifier;

    @ModifyVariable(method = "getTooltipLines", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private AttributeModifier captureModifier(AttributeModifier modifier) {
        return this.modifier = modifier;
    }

    @ModifyVariable(method = "getTooltipLines", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private boolean normalizeCustomAttributes(boolean green) {
        return AttributeModifierIdentifiers.isReserved(this.modifier.getId()) || green;
    }

    /*
    @ModifyVariable(method = "getTooltipLines", at = @At(value = "STORE", ordinal = 2), index = 16, name = "g")
    private double convertToPercentage(double value) {
        return this.modifier.getOperation() == AttributeModifierOperations.percentageAddition ? 100 * value : value;
    }
    */

    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getAttributeModifiers(Lnet/minecraft/inventory/EquipmentSlotType;)Lcom/google/common/collect/Multimap;"))
    private Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlotType slot, PlayerEntity player) {
        return stack.getItem() instanceof SoulboundItem && player != null ? StorageType.get(player, stack.getItem()).get().attributeModifiers(slot) : stack.getAttributeModifiers(slot);
    }
}
