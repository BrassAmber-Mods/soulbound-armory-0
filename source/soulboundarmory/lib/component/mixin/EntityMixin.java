package soulboundarmory.lib.component.mixin;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.lib.component.Component;
import soulboundarmory.lib.component.ComponentKey;
import soulboundarmory.lib.component.access.EntityAccess;
import soulboundarmory.util.Util;

@Mixin(Entity.class)
abstract class EntityMixin implements EntityAccess {
    @Unique
    private final Map<ComponentKey<?>, Component> components = new Reference2ReferenceOpenHashMap<>();

    @Override
    public Map<ComponentKey<?>, Component> soulboundarmory$components() {
        return this.components;
    }

    @Inject(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    public void serializeComponents(NbtCompound tag, CallbackInfoReturnable<NbtCompound> info) {
        var components = new NbtCompound();
        this.components.forEach((key, component) -> components.put(key.key, component.serialize()));
        tag.put(SoulboundArmory.componentKey, components);
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    public void deserializeComponents(NbtCompound tag, CallbackInfo info) {
        Util.ifPresent(tag, SoulboundArmory.componentKey, components -> this.components.forEach((key, component) -> Util.ifPresent(components, key.key, component::deserialize)));
    }
}
