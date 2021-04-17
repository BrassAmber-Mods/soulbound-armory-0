package user11681.usersmanual.entity;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import user11681.usersmanual.asm.duck.entity.BossEntityDuck;
import user11681.usersmanual.server.ServerUtil;

public class EntityUtil {
    public static double getVelocity(final Entity entity) {
        final Vec3d velocity = entity.getVelocity();

        return Math.sqrt(velocity.x * velocity.x
                + velocity.y * velocity.y
                + velocity.z * velocity.z
        );
    }

    public static Entity getEntity(final UUID id) {
        for (final ServerWorld world : ServerUtil.getServer().getWorlds()) {
            final Entity entity = world.getEntity(id);

            if (entity != null) {
                return entity;
            }
        }

        return null;
    }

    public static boolean isBoss(final Entity entity) {
        return ((BossEntityDuck) entity).isBoss();
    }
}
