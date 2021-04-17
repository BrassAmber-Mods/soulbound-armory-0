package user11681.soulboundarmory.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;

public abstract class SoulboundMeleeWeaponItem extends SwordItem implements SoulboundWeaponItem {
    protected final float reach;
    protected final float attackSpeed;

    public SoulboundMeleeWeaponItem(final int attackDamage, final float attackSpeed, final float reach) {
        super(ModToolMaterials.SOULBOUND, attackDamage, attackSpeed, new Item.Settings());

        this.reach = reach;
        this.attackSpeed = attackSpeed;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(final EquipmentSlot slot) {
        final Multimap<EntityAttribute, EntityAttributeModifier> modifiers = HashMultimap.create();

        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", this.attackSpeed, EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.getAttackDamage(), EntityAttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }
}
