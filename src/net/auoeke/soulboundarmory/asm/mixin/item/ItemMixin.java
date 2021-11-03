package net.auoeke.soulboundarmory.asm.mixin.item;

import java.util.UUID;
import net.auoeke.soulboundarmory.util.AttributeModifierIdentifiers;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class)
abstract class ItemMixin {
    @Shadow
    @Final
    protected static UUID ATTACK_SPEED_MODIFIER_ID;

    @Shadow
    @Final
    protected static UUID ATTACK_DAMAGE_MODIFIER_ID;

    static {
        AttributeModifierIdentifiers.attackDamageModifier = ATTACK_DAMAGE_MODIFIER_ID;
        AttributeModifierIdentifiers.attackSpeedModifier = ATTACK_SPEED_MODIFIER_ID;
    }
}
