package user11681.usersmanual.asm.mixin.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import user11681.usersmanual.asm.duck.entity.BossEntityDuck;

@Mixin(Entity.class)
public abstract class EntityMixin implements BossEntityDuck {
    private static final Map<EntityType<?>, Boolean> REGISTRY = new HashMap<>();

    private final Entity self = (Entity) (Object) this;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void constructor(final EntityType<?> type, final World world, final CallbackInfo info) {
        if (!REGISTRY.containsKey(type)) {
            Class<?> klass = self.getClass();

            while (klass != null) {
                for (final Field field : klass.getDeclaredFields()) {
                    field.setAccessible(true);

                    if (BossBar.class.isAssignableFrom(field.getType())) {
                        REGISTRY.put(type, true);
                    }
                }

                klass = klass.getSuperclass();
            }
        }
    }

    @Override
    public final boolean isBoss() {
        return REGISTRY.getOrDefault(self.getType(), false);
    }

    @Override
    public final void setBoss(final boolean boss) {
        REGISTRY.put(self.getType(), boss);
    }
}
