package soulboundarmory.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.item.Item;

public final class AttributeModifierIdentifiers {
    private static final Set<UUID> reserved = new HashSet<>();

    public static UUID reserve(String uuid) {
        return reserve(UUID.fromString(uuid));
    }

    public static UUID reserve(UUID uuid) {
        reserved.add(uuid);

        return uuid;
    }

    public static UUID get(UUID equal) {
        for (var uuid : reserved) {
            if (uuid.equals(equal)) {
                return uuid;
            }
        }

        return null;
    }

    public static boolean isReserved(UUID uuid) {
        return reserved.contains(uuid);
    }

    public static class ItemAccess extends Item {
        public static final UUID attackDamageModifier = BASE_ATTACK_DAMAGE_UUID;
        public static final UUID attackSpeedModifier = BASE_ATTACK_SPEED_UUID;

        public ItemAccess(Properties __) {
            super(__);
        }
    }
}
