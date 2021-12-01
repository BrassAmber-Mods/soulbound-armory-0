package soulboundarmory.util;

import java.util.UUID;
import soulboundarmory.mixin.access.entity.EntityAccess;
import net.minecraft.entity.Entity;

public class EntityUtil {
    public static double speed(Entity entity) {
        var velocity = entity.getDeltaMovement();
        return Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y + velocity.z * velocity.z);
    }

    public static Entity entity(UUID id) {
        for (var world : Util.server().getAllLevels()) {
            var entity = world.getEntity(id);

            if (entity != null) {
                return entity;
            }
        }

        return null;
    }

    public static boolean isBoss(Entity entity) {
        return ((EntityAccess) entity).soulboundarmory$isBoss();
    }
}
