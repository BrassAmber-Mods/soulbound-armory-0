package soulboundarmory.mixin.mixin.access;

import java.util.Map;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("ConstantConditions")
@Mixin(Color.class)
public interface ColorAccess {
    @Accessor("LEGACY_FORMAT_TO_COLOR")
    static Map<TextFormatting, Color> formattingColors() {
        throw null;
    }

    @Invoker("<init>")
    static Color instantiate(int color, String name) {
        throw null;
    }
}
