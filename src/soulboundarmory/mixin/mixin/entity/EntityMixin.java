package soulboundarmory.mixin.mixin.entity;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import java.util.Map;
import net.auoeke.reflect.Fields;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.component.Component;
import soulboundarmory.component.ComponentKey;
import soulboundarmory.mixin.access.entity.EntityAccess;

@SuppressWarnings("ConstantConditions")
@Mixin(Entity.class)
abstract class EntityMixin implements EntityAccess {
    private static final Reference2BooleanMap<EntityType<?>> bosses = new Reference2BooleanOpenHashMap<>();

    @Unique
    private final Map<ComponentKey<?, ?>, Component> components = new Object2ReferenceOpenHashMap<>();

    @Shadow
    public abstract EntityType<?> getType();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void constructor(EntityType<?> type, World world, CallbackInfo info) {
        var entity = (Entity) (Object) this;

        if (!bosses.containsKey(type)) {
            Class<?> klass = entity.getClass();

            while (klass != null) {
                for (var field : Fields.fields(klass)) {
                    if (BossInfo.class.isAssignableFrom(field.getType())) {
                        bosses.put(type, true);

                        break;
                    }
                }

                klass = klass.getSuperclass();
            }
        }
    }

    @Override
    public boolean soulboundarmory$isBoss() {
        return bosses.getOrDefault(this.getType(), false);
    }

    @Override
    public Map<ComponentKey<?, ?>, Component> soulboundarmory$components() {
        return this.components;
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundNBT;)V"))
    public void serializeComponents(CompoundNBT tag, CallbackInfoReturnable<CompoundNBT> info) {
        this.components.forEach((key, component) -> tag.put(key.id.toString(), component.serialize()));
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundNBT;)V"))
    public void deserializeComponents(CompoundNBT tag, CallbackInfo info) {
        this.components.forEach((key, component) -> {
            var componentTag = (CompoundNBT) tag.get(key.id.toString());

            if (componentTag != null) {
                component.deserialize(componentTag);
            }
        });
    }
}
