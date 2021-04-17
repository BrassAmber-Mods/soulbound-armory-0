package user11681.soulboundarmory.entity;

import java.util.UUID;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.usersmanual.entity.AttributeModifierIdentifiers;

public final class SoulboundArmoryAttributes {
    public static final EntityAttribute GENERIC_EFFICIENCY = generic("efficiency", 0, 0, Double.MAX_VALUE).setTracked(true);
    public static final EntityAttribute GENERIC_CRITICAL_STRIKE_PROBABILITY = generic("critical_strike_probability", 1, 0, 1).setTracked(true);

    public static final UUID ATTACK_RANGE_MODIFIER_UUID = AttributeModifierIdentifiers.reserve("F136C871-E55A-4DB5-A8FE-8EA49D9B5B81");
    public static final UUID CRITICAL_STRIKE_PROBABILITY_MODIFIER_ID = AttributeModifierIdentifiers.reserve("B6030C26-AEB4-4AF4-8770-4B365BD1CEB9");
    public static final UUID EFFICIENCY_MODIFIER_ID = AttributeModifierIdentifiers.reserve("77B69417-2F5B-48DB-BD4F-94544760F7A1");
    public static final UUID REACH_MODIFIER_UUID = AttributeModifierIdentifiers.reserve("2D4AA65A-4A15-4C46-9F6B-D3898AEC42B6");

    @SuppressWarnings("SameParameterValue")
    private static ClampedEntityAttribute generic(final String name, final double fallback, final double min, final double max) {
        return Registry.register(Registry.ATTRIBUTE, new Identifier(SoulboundArmory.ID, name), new ClampedEntityAttribute(String.format("generic.%s.%s", SoulboundArmory.ID, name), fallback, min, max));
    }
}
