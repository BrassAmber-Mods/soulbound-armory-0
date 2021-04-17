package user11681.soulboundarmory.asm.mixin.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.item.SoulboundItem;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
    private Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(final ItemStack self, final EquipmentSlot slot, final PlayerEntity player) {
        return self.getItem() instanceof SoulboundItem && player != null ? StorageType.get(player, self.getItem()).getAttributeModifiers(slot) : self.getAttributeModifiers(slot);
    }
}
