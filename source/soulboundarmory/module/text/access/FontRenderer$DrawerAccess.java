package soulboundarmory.module.text.access;

import java.util.List;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Matrix4f;

public interface FontRenderer$DrawerAccess {
	int light();

	float brightnessMultiplier();

	float r();

	float g();

	float b();

	float a();

	float x();

	float y();

	boolean shadow();

	Matrix4f pose();

	List<GlyphRenderer.Rectangle> rectangles();

	VertexConsumerProvider vertexConsumers();

	float invokeFinish(int underlineColor, float x);

	void invokeAddRectangle(GlyphRenderer.Rectangle rectangle);
}
