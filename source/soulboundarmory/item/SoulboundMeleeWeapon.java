package soulboundarmory.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;

public abstract class SoulboundMeleeWeapon extends SwordItem implements SoulboundWeaponItem {
    protected final float reach;
    protected final float attackSpeed;

    public SoulboundMeleeWeapon(int attackDamage, float attackSpeed, float reach) {
        super(SoulboundItems.material, attackDamage, attackSpeed, new Settings().group(ItemGroup.COMBAT));

        this.reach = reach;
        this.attackSpeed = attackSpeed;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        var modifiers = HashMultimap.<EntityAttribute, EntityAttributeModifier>create();

        if (slot == EquipmentSlot.MAINHAND) {
            modifiers.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", this.attackSpeed, EntityAttributeModifier.Operation.ADDITION));
            modifiers.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.getAttackDamage(), EntityAttributeModifier.Operation.ADDITION));
        }

        return modifiers;
    }
}
