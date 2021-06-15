package user11681.soulboundarmory.entity;

import java.util.UUID;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.util.AttributeModifierIdentifiers;

public final class SAAttributes {
    public static final Attribute efficiency = generic("efficiency", 0, Double.MAX_VALUE).setSyncable(true);
    public static final Attribute criticalStrikeRate = generic("critical_strike_rate", 1, 1).setSyncable(true);

    public static final UUID attackRangeUUID = AttributeModifierIdentifiers.reserve("F136C871-E55A-4DB5-A8FE-8EA49D9B5B81");
    public static final UUID criticalStrikeRateUUID = AttributeModifierIdentifiers.reserve("B6030C26-AEB4-4AF4-8770-4B365BD1CEB9");
    public static final UUID efficiencyUUID = AttributeModifierIdentifiers.reserve("77B69417-2F5B-48DB-BD4F-94544760F7A1");
    public static final UUID reachUUID = AttributeModifierIdentifiers.reserve("2D4AA65A-4A15-4C46-9F6B-D3898AEC42B6");

    private static RangedAttribute generic(String name, double fallback, double max) {
        return new RangedAttribute(String.format("generic.%s.%s", SoulboundArmory.ID, name), fallback, 0, max);
    }
}
