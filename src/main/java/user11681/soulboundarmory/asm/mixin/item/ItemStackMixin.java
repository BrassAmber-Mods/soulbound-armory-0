package user11681.soulboundarmory.asm.mixin.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.util.AttributeModifierIdentifiers;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {
    private EntityAttributeModifier modifier;

    @ModifyVariable(method = "getTooltip", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private EntityAttributeModifier captureModifier(EntityAttributeModifier modifier) {
        return this.modifier = modifier;
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private boolean normalizeCustomAttributes(boolean green) {
        return AttributeModifierIdentifiers.isReserved(this.modifier.getId()) || green;
    }

    /*
    @ModifyVariable(method = "getTooltipLines", at = @At(value = "STORE", ordinal = 2), index = 16, name = "g")
    private double convertToPercentage(double value) {
        return this.modifier.getOperation() == AttributeModifierOperations.percentageAddition ? 100 * value : value;
    }
    */

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getAttributeModifiers(Lnet/minecraft/inventory/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
    private Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot, PlayerEntity player) {
        return stack.getItem() instanceof SoulboundItem && player != null ? StorageType.get(player, stack.getItem()).attributeModifiers(slot) : stack.getAttributeModifiers(slot);
    }
}
