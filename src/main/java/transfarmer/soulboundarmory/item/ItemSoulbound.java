package transfarmer.soulboundarmory.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import transfarmer.farmerlib.util.ReflectUtil;

import java.util.UUID;

import static net.minecraft.entity.EquipmentSlot.MAINHAND;

public interface ItemSoulbound {
    UUID ATTACK_SPEED_MODIFIER = ReflectUtil.getAttackSpeedModifier();
    UUID ATTACK_DAMAGE_MODIFIER = ReflectUtil.getAttackDamageModifier();

    default int getMainhandAttributeEntries(final ItemStack itemStack, final PlayerEntity player) {
        int entries = 0;

        for (final EntityAttributeModifier modifier : itemStack.getAttributeModifiers(MAINHAND).values()) {
            double amount = modifier.getAmount();
            boolean flag = false;

            if (modifier.getId() == ATTACK_DAMAGE_MODIFIER) {
                amount += player.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getBaseValue();
                amount += EnchantmentHelper.getAttackDamage(itemStack, EntityGroup.DEFAULT);
                flag = true;
            } else if (modifier.getId() == ATTACK_SPEED_MODIFIER) {
                amount += player.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getBaseValue();
                flag = true;
            }

            if (flag || amount != 0D) {
                entries++;
            }
        }

        return entries;
    }
}
