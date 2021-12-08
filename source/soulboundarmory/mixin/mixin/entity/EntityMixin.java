package soulboundarmory.mixin.mixin.entity;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.component.Component;
import soulboundarmory.component.ComponentKey;
import soulboundarmory.mixin.access.entity.EntityAccess;

@Mixin(Entity.class)
abstract class EntityMixin implements EntityAccess {
    @Unique
    private static final String key = SoulboundArmory.id("components").toString();

    @Unique
    private final Map<ComponentKey<?, ?>, Component> components = new Object2ReferenceOpenHashMap<>();

    @Shadow
    public abstract EntityType<?> getType();

    @Override
    public Map<ComponentKey<?, ?>, Component> soulboundarmory$components() {
        return this.components;
    }

    @Inject(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    public void serializeComponents(NbtCompound tag, CallbackInfoReturnable<NbtCompound> info) {
        var components = new NbtCompound();
        this.components.forEach((key, component) -> components.put(key.id.toString(), component.serialize()));
        tag.put(key, components);
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    public void deserializeComponents(NbtCompound tag, CallbackInfo info) {
        var components = (NbtCompound) tag.get(key);

        if (components != null) {
            this.components.forEach((key, component) -> {
                var componentTag = (NbtCompound) components.get(key.id.toString());

                if (componentTag != null) {
                    component.deserialize(componentTag);
                }
            });
        }
    }
}
