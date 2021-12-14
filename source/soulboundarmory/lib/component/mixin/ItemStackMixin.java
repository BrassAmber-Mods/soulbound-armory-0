package soulboundarmory.lib.component.mixin;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.lib.component.ComponentRegistry;
import soulboundarmory.lib.component.ItemStackComponent;
import soulboundarmory.lib.component.ItemStackComponentKey;
import soulboundarmory.lib.component.access.ItemStackAccess;
import soulboundarmory.util.Util;

@Mixin(ItemStack.class)
abstract class ItemStackMixin implements ItemStackAccess {
    @Unique
    private Map<ItemStackComponentKey<?>, ItemStackComponent<?>> components;

    @Override
    public ItemStackComponent<?> soulboundarmory$component(ItemStackComponentKey<?> key) {
        return this.components == null ? null : this.components.get(key);
    }

    @Override
    public ItemStackComponent<?> soulboundarmory$component(ItemStackComponentKey<?> key, ItemStackComponent<?> component) {
        return this.components().put(key, component);
    }

    @Unique
    private Map<ItemStackComponentKey<?>, ItemStackComponent<?>> components() {
        return this.components == null ? this.components = new Reference2ReferenceOpenHashMap<>() : this.components;
    }

    @Inject(method = {"<init>(Lnet/minecraft/item/ItemConvertible;ILnet/minecraft/nbt/NbtCompound;)V", "<init>(Lnet/minecraft/nbt/NbtCompound;)V"}, at = @At(value = "RETURN"))
    private void addComponents(CallbackInfo info) {
        for (var key : ComponentRegistry.item.values()) {
            if (key.predicate == null || key.predicate.test(Util.cast(this))) {
                key.attach((ItemStack) (Object) this);
            }
        }

        if (this.components != null) {
            this.components.values().forEach(ItemStackComponent::initialize);
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("RETURN"))
    private void deserializeComponents(NbtCompound tag, CallbackInfo info) {
        if (this.components != null) {
            Util.ifPresent(tag, SoulboundArmory.componentKey, components -> this.components.forEach((key, component) -> Util.ifPresent(components, key.key, component::deserialize)));
        }
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    private void serializeComponents(NbtCompound tag, CallbackInfoReturnable<NbtCompound> info) {
        if (this.components != null) {
            var components = new NbtCompound();
            this.components.forEach((key, component) -> components.put(key.key, component.serialize()));
            tag.put(SoulboundArmory.componentKey, components);
        }
    }

    @ModifyVariable(method = "copy", at = @At(value = "STORE"), ordinal = 1)
    private ItemStack copyComponents(ItemStack copy) {
        if (this.components != null) {
            this.components.forEach((key, component) -> component.copy(Util.cast(key.of(copy))));
        }

        return copy;
    }

    @Inject(method = "isEqual", at = @At("RETURN"), cancellable = true)
    private void compareComponents(ItemStack other, CallbackInfoReturnable<Boolean> info) {
        if (this.components != null && info.getReturnValueZ()) {
            info.setReturnValue(this.components.entrySet().stream().allMatch(entry -> {
                var component = entry.getKey().of(other);
                return component != null && entry.getValue().equals(Util.cast(component));
            }));
        }
    }

    @Inject(method = "areTagsEqual", at = @At("RETURN"), cancellable = true)
    private static void compareComponents(ItemStack stack, ItemStack other, CallbackInfoReturnable<Boolean> info) {
        var mixin = (ItemStackMixin) (Object) stack;

        if (mixin.components != null && info.getReturnValueZ()) {
            info.setReturnValue(mixin.components.entrySet().stream().allMatch(entry -> {
                var component = entry.getKey().of(other);
                return component != null && entry.getValue().equals(Util.cast(component));
            }));
        }
    }
}
