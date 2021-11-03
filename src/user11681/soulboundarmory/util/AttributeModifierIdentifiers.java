package user11681.soulboundarmory.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class AttributeModifierIdentifiers {
    private static final Set<UUID> reserved = new HashSet<>();

    public static UUID attackDamageModifier;
    public static UUID attackSpeedModifier;

    public static UUID reserve(String uuid) {
        return reserve(UUID.fromString(uuid));
    }

    public static UUID reserve(UUID uuid) {
        reserved.add(uuid);

        return uuid;
    }

    public static UUID get(UUID equal) {
        for (UUID uuid : reserved) {
            if (uuid.equals(equal)) {
                return uuid;
            }
        }

        return null;
    }

    public static boolean isReserved(UUID uuid) {
        return reserved.contains(uuid);
    }
}
