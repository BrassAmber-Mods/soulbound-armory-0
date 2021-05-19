package user11681.soulboundarmory.asm.mixin.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlotType;
import net.minecraft.entity.attribute.Attribute;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.item.SoulboundItem;
import user11681.soulboundarmory.util.AttributeModifierIdentifiers;
import user11681.soulboundarmory.util.AttributeModifierOperations;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    private AttributeModifier modifier;

    @ModifyVariable(method = "getTooltip", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private AttributeModifier captureModifier(AttributeModifier modifier) {
        return this.modifier = modifier;
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private boolean normalizeCustomAttributes(boolean green) {
        return AttributeModifierIdentifiers.isReserved(this.modifier.getId()) || green;
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE", ordinal = 2), index = 16, name = "g")
    private double convertToPercentage(double value) {
        return this.modifier.getOperation() == AttributeModifierOperations.percentageAddition ? 100 * value : value;
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlotType;)Lcom/google/common/collect/Multimap;"))
    private Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack self, final EquipmentSlotType slot, final PlayerEntity player) {
        return self.getItem() instanceof SoulboundItem && player != null ? StorageType.get(player, self.getItem()).getAttributeModifiers(slot) : self.getAttributeModifiers(slot);
    }
}
