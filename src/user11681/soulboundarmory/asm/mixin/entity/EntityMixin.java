package user11681.soulboundarmory.asm.mixin.entity;

import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import java.lang.reflect.Field;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import user11681.reflect.Fields;
import user11681.soulboundarmory.asm.access.entity.BossEntityAccess;

@SuppressWarnings("ConstantConditions")
@Mixin(Entity.class)
abstract class EntityMixin implements BossEntityAccess {
    private static final Reference2BooleanMap<EntityType<?>> registry = new Reference2BooleanOpenHashMap<>();

    @Shadow
    public abstract EntityType<?> getType();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void constructor(EntityType<?> type, World world, CallbackInfo info) {
        Entity entity = (Entity) (Object) this;

        if (!registry.containsKey(type)) {
            Class<?> klass = entity.getClass();

            while (klass != null) {
                for (Field field : Fields.fields(klass)) {
                    if (BossBar.class.isAssignableFrom(field.getType())) {
                        registry.put(type, true);
                    }
                }

                klass = klass.getSuperclass();
            }
        }
    }

    @Override
    public boolean isBoss() {
        return registry.putIfAbsent(this.getType(), false);
    }

    @Override
    public void setBoss(boolean boss) {
        registry.put(this.getType(), boss);
    }
}
