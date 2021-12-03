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
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
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
    private final ReferenceOpenHashSet<Formatting> formattings = new ReferenceOpenHashSet<>();

    @Unique
    private static Formatting switchFormatting;

    @Unique
    private static Boolean previousObfuscated;

    @Override
    @Unique
    public Set<Formatting> formattings() {
        return this.formattings;
    }

    @Override
    public boolean has(Formatting formatting) {
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
    public void add(Formatting formatting) {
        this.formattings.add(formatting);
    }

    @Inject(method = "withColor(Lnet/minecraft/text/TextColor;)Lnet/minecraft/text/Style;", at = @At("RETURN"))
    public void withColor(TextColor color, CallbackInfoReturnable<Style> info) {
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
    @Inject(method = "withUnderline", at = @At("RETURN"))
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
    public void withFont(Identifier font, CallbackInfoReturnable<Style> info) {
        this.add(info.getReturnValue());
    }

    @Inject(method = {"withFormatting(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/Style;", "withExclusiveFormatting"}, at = @At(value = "TAIL"))
    public void addFormatting(Formatting formatting, CallbackInfoReturnable<Style> info) {
        this.add(info.getReturnValue());

        if ((Object) formatting instanceof ExtendedFormatting) {
            ((ExtendedStyle) info.getReturnValue()).add(formatting);
        }
    }

    @SuppressWarnings({"unused", "RedundantSuppression"}) // invoked by handwritten bytecode
    private static int phormat_hackOrdinal(Formatting formatting) {
        return (Object) (switchFormatting = formatting) instanceof ExtendedFormatting && !formatting.isColor() ? Formatting.OBFUSCATED.ordinal() : formatting.ordinal();
    }

    @ModifyVariable(method = {"withExclusiveFormatting", "withFormatting(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/Style;", "withFormatting([Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/Style;"},
                    at = @At(value = "CONSTANT", args = "intValue=1", ordinal = 0),
                    ordinal = 4)
    public Boolean saveObfuscated(Boolean previous) {
        return previousObfuscated = previous;
    }

    @ModifyVariable(method = {"withExclusiveFormatting", "withFormatting(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/Style;", "withFormatting([Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/Style;"},
                    at = @At(value = "STORE", ordinal = 1),
                    ordinal = 4)
    public Boolean fixObfuscated(Boolean previous) {
        return switchFormatting == Formatting.OBFUSCATED || previousObfuscated == Boolean.TRUE;
    }

    @Inject(method = "withFormatting([Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/Style;", at = @At(value = "TAIL"))
    public void addFormatting(Formatting[] formattings, CallbackInfoReturnable<Style> info) {
        var style = (ExtendedStyle) info.getReturnValue();

        for (var formatting : formattings) {
            if ((Object) formatting instanceof ExtendedFormatting) {
                style.add(formatting);
            }
        }
    }

    @Inject(method = "withParent", at = @At("TAIL"))
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
        info.setReturnValue(info.getReturnValue().replace("}", ", phormat:formatting=" + Arrays.toString(this.formattings.toArray(new Formatting[0])) + '}'));
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
        @Inject(method = "deserialize*", at = @At(value = "NEW", target = "net/minecraft/text/Style"))
        public void deserializeCustomFormatting(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext, CallbackInfoReturnable<Style> info) {
            var style = (ExtendedStyle) info.getReturnValue();
            var object = jsonElement.getAsJsonObject();

            if (object.has("format")) {
                for (var phormat : object.getAsJsonArray("format")) {
                    style.add(Formatting.byName(phormat.getAsString()));
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
                    formatting.add(phormat.getName());
                }
            }

            object.add("format", formatting);
        }
    }
}
