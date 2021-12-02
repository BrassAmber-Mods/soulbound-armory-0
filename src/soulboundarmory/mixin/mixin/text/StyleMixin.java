package soulboundarmory.mixin.mixin.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import soulboundarmory.mixin.access.ExtendedStyle;
import soulboundarmory.text.format.ExtendedFormatting;

@SuppressWarnings("ConstantConditions")
@Mixin(Style.class)
abstract class StyleMixin implements ExtendedStyle {
    @Unique
    private final ReferenceOpenHashSet<TextFormatting> formattings = new ReferenceOpenHashSet<>();

    @Unique
    private static TextFormatting switchFormatting;

    @Unique
    private static Boolean previousObfuscated;

    @Override
    @Unique
    public Set<TextFormatting> formattings() {
        return this.formattings;
    }

    @Override
    public boolean has(TextFormatting formatting) {
        return this.formattings.contains(formatting);
    }

    @Override
    public void add(Style to) {
        this.add((ExtendedStyle) to);
    }

    @Unique
    private void add(ExtendedStyle style) {
        style.add(this.formattings);
    }

    @Override
    public void add(TextFormatting formatting) {
        this.formattings.add(formatting);
    }

    @Inject(method = "withColor(Lnet/minecraft/util/text/Color;)Lnet/minecraft/util/text/Style;", at = @At("RETURN"))
    public void withColor(Color color, CallbackInfoReturnable<Style> info) {
        this.add(info.getReturnValue());
    }

    @Inject(method = "withBold", at = @At("RETURN"))
    public void withBold(Boolean bold, CallbackInfoReturnable<Style> info) {
        this.add(info.getReturnValue());
    }

    @Inject(method = "withItalic", at = @At("RETURN"))
    public void withItalic(Boolean italic, CallbackInfoReturnable<Style> info) {
        this.add(info.getReturnValue());
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "withUnderlined", at = @At("RETURN"))
    public void withUnderline(Boolean underline, CallbackInfoReturnable<Style> info) {
        this.add(info.getReturnValue());
    }

    @Inject(method = "withClickEvent", at = @At("RETURN"))
    public void withClickEvent(ClickEvent clickEvent, CallbackInfoReturnable<Style> info) {
        this.add(info.getReturnValue());
    }

    @Inject(method = "withHoverEvent", at = @At("RETURN"))
    public void withHoverEvent(HoverEvent hoverEvent, CallbackInfoReturnable<Style> info) {
        this.add(info.getReturnValue());
    }

    @Inject(method = "withInsertion", at = @At("RETURN"))
    public void withInsertion(String insertion, CallbackInfoReturnable<Style> info) {
        this.add(info.getReturnValue());
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "withFont", at = @At("RETURN"))
    public void withFont(ResourceLocation font, CallbackInfoReturnable<Style> info) {
        this.add(info.getReturnValue());
    }

    @Inject(method = {"withColor(Lnet/minecraft/util/text/TextFormatting;)Lnet/minecraft/util/text/Style;", "applyLegacyFormat"}, at = @At(value = "TAIL"))
    public void addFormatting(TextFormatting formatting, CallbackInfoReturnable<Style> info) {
        this.add(info.getReturnValue());

        if ((Object) formatting instanceof ExtendedFormatting) {
            ((ExtendedStyle) info.getReturnValue()).add(formatting);
        }
    }

    @SuppressWarnings({"unused", "RedundantSuppression"}) // invoked by handwritten bytecode
    private static int phormat_hackOrdinal(TextFormatting formatting) {
        return (Object) (switchFormatting = formatting) instanceof ExtendedFormatting && !formatting.isColor() ? TextFormatting.OBFUSCATED.ordinal() : formatting.ordinal();
    }

    @ModifyVariable(method = {"applyLegacyFormat", "applyFormat", "applyFormats"},
                    at = @At(value = "CONSTANT", args = "intValue=1", ordinal = 0),
                    ordinal = 4)
    public Boolean saveObfuscated(Boolean previous) {
        return previousObfuscated = previous;
    }

    @ModifyVariable(method = {"applyLegacyFormat", "applyFormat", "applyFormats"}, at = @At(value = "STORE", ordinal = 1), ordinal = 4)
    public Boolean fixObfuscated(Boolean previous) {
        return switchFormatting == TextFormatting.OBFUSCATED || previousObfuscated == Boolean.TRUE;
    }

    @Inject(method = "applyFormats", at = @At(value = "TAIL"))
    public void addFormatting(TextFormatting[] formattings, CallbackInfoReturnable<Style> info) {
        var style = (ExtendedStyle) info.getReturnValue();

        for (var formatting : formattings) {
            if ((Object) formatting instanceof ExtendedFormatting) {
                style.add(formatting);
            }
        }
    }

    @Inject(method = "applyTo", at = @At("TAIL"))
    public void withParent(Style parent, CallbackInfoReturnable<Style> info) {
        if (parent != Style.EMPTY) {
            var child = (ExtendedStyle) info.getReturnValue();

            for (var formatting : ((StyleMixin) (Object) parent).formattings) {
                if (!child.has(formatting)) {
                    child.add(formatting);
                }
            }
        }
    }

    @Inject(method = "toString", at = @At("RETURN"), cancellable = true)
    public void appendFormatting(CallbackInfoReturnable<String> info) {
        info.setReturnValue(info.getReturnValue().replace("}", ", phormat:formatting=" + Arrays.toString(this.formattings.toArray(new TextFormatting[0])) + '}'));
    }

    @Inject(method = "equals", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    public void compareCustomFormatting(Object obj, CallbackInfoReturnable<Boolean> info) {
        var otherFormattings = ((ExtendedStyle) obj).formattings();
        var formattings = this.formattings;

        if (otherFormattings.size() != formattings.size()) {
            info.setReturnValue(false);
        } else for (var formatting : otherFormattings) {
            if (!formattings.contains(formatting)) {
                info.setReturnValue(false);

                return;
            }
        }
    }

    @ModifyArg(method = "hashCode", at = @At(value = "INVOKE", target = "Ljava/util/Objects;hash([Ljava/lang/Object;)I"))
    public Object[] hashCustomFormatting(Object[] previous) {
        var formattings = this.formattings.toArray();
        var previousLength = previous.length;
        var formattingCount = formattings.length;
        var all = Arrays.copyOf(previous, previousLength + formattingCount);
        System.arraycopy(formattings, 0, all, previousLength, formattingCount);

        return all;
    }

    @Mixin(Style.Serializer.class)
    abstract static class SerializerMixin {
        @Inject(method = "deserialize*", at = @At(value = "NEW", target = "net/minecraft/util/text/Style"))
        public void deserializeCustomFormatting(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<Style> info) {
            var style = (ExtendedStyle) info.getReturnValue();
            var object = jsonElement.getAsJsonObject();

            if (object.has("format")) {
                for (var phormat : object.getAsJsonArray("format")) {
                    style.add(TextFormatting.getValueByName(phormat.getAsString()));
                }
            }
        }

        @Inject(method = "serialize*", at = @At("RETURN"), cancellable = true)
        public void serializeCustomFormatting(Style style, Type type, JsonSerializationContext jsonSerializationContext, CallbackInfoReturnable<JsonElement> info) {
            var object = (JsonObject) info.getReturnValue();

            if (object == null) {
                info.setReturnValue(object = new JsonObject());
            }

            var formatting = new JsonArray();

            for (var phormat : ((ExtendedStyle) style).formattings()) {
                if ((Object) phormat instanceof ExtendedFormatting) {
                    formatting.add(phormat.getFriendlyName());
                }
            }

            object.add("format", formatting);
        }
    }
}
