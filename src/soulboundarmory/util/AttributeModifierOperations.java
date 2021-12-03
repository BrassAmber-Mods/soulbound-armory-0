package soulboundarmory.util;

import net.auoeke.reflect.EnumConstructor;
import net.minecraft.entity.attribute.EntityAttributeModifier;

public class AttributeModifierOperations {
    public static final EntityAttributeModifier.Operation percentageAddition = EnumConstructor.add(EntityAttributeModifier.Operation.class, "PERCENTAGE_ADDITION", 0x209365);
}
