package soulboundarmory.util;

import java.util.UUID;
import soulboundarmory.mixin.access.entity.EntityAccess;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class EntityUtil {
    public static double speed(Entity entity) {
        Vec3d velocity = entity.getVelocity();
        return Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y + velocity.z * velocity.z);
    }

    public static Entity entity(UUID id) {
        for (ServerWorld world : Util.server().getWorlds()) {
            Entity entity = world.getEntity(id);

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
