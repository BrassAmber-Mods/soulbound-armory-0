package soulboundarmory.mixin.access;

import java.util.List;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Matrix4f;

public interface CharacterRendererAccess {
    int light();

    float dimFactor();

    float r();

    float g();

    float b();

    float a();

    float x();

    float y();

    boolean shadow();

    boolean translucent();

    Matrix4f pose();

    List<GlyphRenderer.Rectangle> rectangles();

    VertexConsumerProvider bufferSource();

    float invokeFinish(int underlineColor, float x);

    void invokeAddRectangle(GlyphRenderer.Rectangle rectangle);
}
