package soulboundarmory.util;

import java.util.UUID;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import net.auoeke.reflect.Fields;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.boss.BossBar;

public class EntityUtil {
	private static final Reference2BooleanMap<Class<?>> bosses = new Reference2BooleanOpenHashMap<>();

	public static double speed(Entity entity) {
		var velocity = entity.getVelocity();
		return Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y + velocity.z * velocity.z);
	}

	public static double attribute(LivingEntity entity, EntityAttribute attribute) {
		var instance = entity.getAttributeInstance(attribute);
		return instance == null ? 0 : instance.getValue();
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

	private static synchronized boolean isBoss(Class<?> type) {
		if (type == null) {
			return false;
		}

		return bosses.containsKey(type) ? bosses.get(type) : Boolean.valueOf(bosses.put(type, Fields.of(type).anyMatch(field -> BossBar.class.isAssignableFrom(field.getType())) || isBoss(type.getSuperclass())));
	}
}
