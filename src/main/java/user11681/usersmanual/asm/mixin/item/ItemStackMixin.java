package user11681.usersmanual.asm.mixin.item;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import user11681.usersmanual.entity.AttributeModifierIdentifiers;
import user11681.usersmanual.entity.AttributeModifierOperations;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    private EntityAttributeModifier modifier;

    @ModifyVariable(method = "getTooltip", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private EntityAttributeModifier captureModifier(final EntityAttributeModifier modifier) {
        return this.modifier = modifier;
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private boolean normalizeCustomAttributes(final boolean green) {
        return AttributeModifierIdentifiers.isReserved(this.modifier.getId()) || green;
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE", ordinal = 2), index = 16, name = "g")
    private double convertToPercentage(final double value) {
        return this.modifier.getOperation() == AttributeModifierOperations.PERCENTAGE_ADDITION ? 100 * value : value;
    }
}
