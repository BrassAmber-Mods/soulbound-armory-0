package user11681.soulboundarmory.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.item.Item;

public final class AttributeModifierIdentifiers extends Item {
    private static final Set<UUID> RESERVED_IDENTIFIERS = new HashSet<>();

    public static final UUID ATTACK_DAMAGE_MODIFIER_ID = reserve(Item.BASE_ATTACK_DAMAGE_UUID);
    public static final UUID ATTACK_SPEED_MODIFIER_ID = reserve(Item.BASE_ATTACK_SPEED_UUID);

    public static UUID reserve(String uuid) {
        return reserve(UUID.fromString(uuid));
    }

    public static UUID reserve(UUID uuid) {
        RESERVED_IDENTIFIERS.add(uuid);

        return uuid;
    }

    public static UUID get(UUID equal) {
        for (UUID uuid : RESERVED_IDENTIFIERS) {
            if (uuid.equals(equal)) {
                return uuid;
            }
        }

        return null;
    }

    public static boolean isReserved(UUID uuid) {
        return RESERVED_IDENTIFIERS.contains(uuid);
    }

    @SuppressWarnings("ConstantConditions")
    private AttributeModifierIdentifiers() {
        super(null);
    }
}
