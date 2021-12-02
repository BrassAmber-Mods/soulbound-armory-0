package soulboundarmory.mixin.mixin.access;

import java.util.Map;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("ConstantConditions")
@Mixin(TextColor.class)
public interface ColorAccess {
    @Accessor("LEGACY_FORMAT_TO_COLOR")
    static Map<Formatting, TextColor> formattingColors() {
        throw null;
    }

    @Invoker("<init>")
    static TextColor instantiate(int color, String name) {
        throw null;
    }
}
