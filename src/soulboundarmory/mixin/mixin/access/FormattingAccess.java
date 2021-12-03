package soulboundarmory.mixin.mixin.access;

import java.util.Map;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Formatting.class)
public interface FormattingAccess {
    @Accessor("BY_NAME")
    static Map<String, Formatting> nameMap() {
        throw null;
    }

    @SuppressWarnings("unused")
    @Invoker("sanitize")
    static String sanitize(String name) {
        throw null;
    }
}
