package soulboundarmory.util;

import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import java.util.UUID;
import net.auoeke.reflect.Fields;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;

public class EntityUtil {
    private static final Reference2BooleanMap<Class<?>> bosses = new Reference2BooleanOpenHashMap<>();

    public static double speed(Entity entity) {
        var velocity = entity.getVelocity();
        return Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y + velocity.z * velocity.z);
    }

    public static Entity entity(UUID id) {
        for (var world : Util.server().getWorlds()) {
            var entity = world.getEntity(id);

            if (entity != null) {
                return entity;
            }
        }

        return null;
    }

    public static boolean isBoss(Entity entity) {
        return isBoss(entity.getClass());
    }

    private static boolean isBoss(Class<?> type) {
        if (type == null) {
            return false;
        }

        return bosses.computeBooleanIfAbsent(type, type1 -> {
            for (var field : Fields.fields(type1)) {
                if (BossBar.class.isAssignableFrom(field.getType())) {
                    return true;
                }
            }

            return isBoss(type1.getSuperclass());
        });
    }
}
