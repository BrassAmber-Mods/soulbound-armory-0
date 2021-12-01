package soulboundarmory.mixin.mixin.text;

import java.util.List;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import soulboundarmory.mixin.access.ExtendedStyle;
import soulboundarmory.mixin.access.FontRenderer$CharacterRendererAccess;
import soulboundarmory.text.format.ExtendedFormatting;

@SuppressWarnings("public-target")
@Mixin(targets = "net.minecraft.client.gui.FontRenderer$CharacterRenderer")
abstract class FontRenderer$CharacterRendererMixin implements FontRenderer$CharacterRendererAccess {
    @Override
    @Accessor("packedLightCoords")
    public abstract int light();

    @Override
    @Accessor("dimFactor")
    public abstract float dimFactor();

    @Override
    @Accessor("r")
    public abstract float r();

    @Override
    @Accessor("g")
    public abstract float g();

    @Override
    @Accessor("b")
    public abstract float b();

    @Override
    @Accessor("a")
    public abstract float a();

    @Override
    @Accessor("x")
    public abstract float x();

    @Override
    @Accessor("y")
    public abstract float y();

    @Override
    @Accessor("dropShadow")
    public abstract boolean shadow();

    @Override
    @Accessor("seeThrough")
    public abstract boolean translucent();

    @Override
    @Accessor("pose")
    public abstract Matrix4f pose();

    @Override
    @Accessor("effects")
    public abstract List<TexturedGlyph.Effect> rectangles();

    @Override
    @Accessor("bufferSource")
    public abstract IRenderTypeBuffer bufferSource();

    @Override
    @Invoker("finish")
    public abstract float invokeFinish(int underlineColor, float x);

    @Override
    @Invoker("addEffect")
    public abstract void invokeAddRectangle(TexturedGlyph.Effect rectangle);

    @Inject(method = "accept(ILnet/minecraft/util/text/Style;I)Z",
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/gui/FontRenderer$CharacterRenderer;x:F"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void formatCustom(
        int charIndex,
        Style style,
        int character,
        CallbackInfoReturnable<Boolean> info,
        Font font,
        IGlyph glyph,
        TexturedGlyph glyphRenderer,
        boolean isBold,
        float alpha,
        Color color,
        float red,
        float green,
        float blue,
        float advance
    ) {
        for (var formatting : ((ExtendedStyle) style).formattings()) {
            if ((Object) formatting instanceof ExtendedFormatting extendedFormatting) {
                var formatter = extendedFormatting.formatter();

                if (formatter != null) {
                    formatter.format(this, style, charIndex, character, font, glyph, glyphRenderer, color, red, green, blue, advance);
                }
            }
        }
    }
}
