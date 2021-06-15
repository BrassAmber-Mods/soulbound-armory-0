package user11681.soulboundarmory.asm.mixin.item;

import java.util.UUID;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import user11681.soulboundarmory.util.AttributeModifierIdentifiers;

@Mixin(Item.class)
abstract class ItemMixin {
    @Shadow
    @Final
    protected static UUID BASE_ATTACK_SPEED_UUID;

    @Shadow
    @Final
    protected static UUID BASE_ATTACK_DAMAGE_UUID;

    static {
        AttributeModifierIdentifiers.attackDamageModifier = BASE_ATTACK_DAMAGE_UUID;
        AttributeModifierIdentifiers.attackSpeedModifier = BASE_ATTACK_SPEED_UUID;
    }
}
