package soulboundarmory.mixin.mixin;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.component.Components;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.item.SoulboundItem;
import soulboundarmory.util.AttributeModifierIdentifiers;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {
    @Unique
    private EntityAttributeModifier modifier;

    @ModifyVariable(method = "getTooltip", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private EntityAttributeModifier captureModifier(EntityAttributeModifier modifier) {
        return this.modifier = modifier;
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private boolean normalizeCustomAttributes(boolean green) {
        return green || AttributeModifierIdentifiers.isReserved(this.modifier.getId());
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
    private Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot, PlayerEntity player) {
        return stack.getItem() instanceof SoulboundItem && player != null ? ItemComponent.get(player, stack).get().attributeModifiers(slot) : stack.getAttributeModifiers(slot);
    }

    @Inject(method = "hasGlint", at = @At("HEAD"), cancellable = true)
    private void disableGlint(CallbackInfoReturnable<Boolean> info) {
        Components.marker.nullable((ItemStack) (Object) this)
            .map(component -> component.item)
            .map(component -> Components.config.of(component.player))
            .filter(component -> !component.glint)
            .ifPresent(component -> info.setReturnValue(false));
    }

    @Inject(method = "postMine", at = @At("RETURN"))
    private void addXP(World world, BlockState state, BlockPos pos, PlayerEntity miner, CallbackInfo info) {
        Components.marker.nullable((ItemStack) (Object) this).ifPresent(marker -> marker.item.mined(state, pos));
    }

    @Inject(method = "isSuitableFor", at = @At("RETURN"), cancellable = true)
    private void checkSoulboundItemHasSuitableMiningLevel(BlockState state, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValueZ()) {
            // Components.marker.nullable(Util.cast(this)).ifPresent(marker -> info.setReturnValue(marker.item.intValue(StatisticType.miningLevel) >= state.getMaterial().getHarvestLevel()));
        }
    }
}
