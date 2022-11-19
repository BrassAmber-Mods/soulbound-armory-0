package soulboundarmory.module.text.mixin;

import java.util.List;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import soulboundarmory.module.text.ExtendedFormatting;
import soulboundarmory.module.text.access.ExtendedStyle;
import soulboundarmory.module.text.access.FontRenderer$DrawerAccess;

@SuppressWarnings("public-target")
@Mixin(targets = "net.minecraft.client.font.TextRenderer$Drawer")
abstract class FontRenderer$DrawerMixin implements FontRenderer$DrawerAccess {
	@Override
	@Accessor("light")
	public abstract int light();

	@Override
	@Accessor("brightnessMultiplier")
	public abstract float brightnessMultiplier();

	@Override
	@Accessor("red")
	public abstract float r();

	@Override
	@Accessor("green")
	public abstract float g();

	@Override
	@Accessor("blue")
	public abstract float b();

	@Override
	@Accessor("alpha")
	public abstract float a();

	@Override
	@Accessor("x")
	public abstract float x();

	@Override
	@Accessor("y")
	public abstract float y();

	@Override
	@Accessor("shadow")
	public abstract boolean shadow();

	@Override
	@Accessor("matrix")
	public abstract Matrix4f pose();

	@Override
	@Accessor("rectangles")
	public abstract List<GlyphRenderer.Rectangle> rectangles();

	@Override
	@Accessor("vertexConsumers")
	public abstract VertexConsumerProvider vertexConsumers();

	@Override
	@Invoker("drawLayer")
	public abstract float invokeFinish(int underlineColor, float x);

	@Override
	@Invoker("addRectangle")
	public abstract void invokeAddRectangle(GlyphRenderer.Rectangle rectangle);

	@Inject(method = "accept",
	        at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/font/TextRenderer$Drawer;x:F"),
	        locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void formatCustom(
		int charIndex,
		Style style,
		int character,
		CallbackInfoReturnable<Boolean> info,
		FontStorage font,
		Glyph glyph,
		GlyphRenderer glyphRenderer,
		boolean isBold,
		float alpha,
		float red,
		float green,
		float blue,
		TextColor color,
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
