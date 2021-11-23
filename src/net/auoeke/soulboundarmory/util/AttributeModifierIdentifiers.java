package net.auoeke.soulboundarmory.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.item.Item;

public final class AttributeModifierIdentifiers extends Item {
    private static final Set<UUID> reserved = new HashSet<>();

    public static final UUID attackDamageModifier = BASE_ATTACK_DAMAGE_UUID;
    public static final UUID attackSpeedModifier = BASE_ATTACK_SPEED_UUID;

    public AttributeModifierIdentifiers(Properties __) {
        super(__);
    }

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
}
