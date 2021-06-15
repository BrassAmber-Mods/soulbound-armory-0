package user11681.soulboundarmory.util;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import user11681.soulboundarmory.asm.access.entity.BossEntityAccess;

public class EntityUtil {
    public static double speed(Entity entity) {
        Vector3d velocity = entity.getDeltaMovement();

        return Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y + velocity.z * velocity.z);
    }

    public static Entity entity(UUID id) {
        for (ServerWorld world : Util.server().getAllLevels()) {
            Entity entity = world.getEntity(id);

            if (entity != null) {
                return entity;
            }
        }

        return null;
    }

    public static boolean isBoss(Entity entity) {
        return ((BossEntityAccess) entity).isBoss();
    }
}
