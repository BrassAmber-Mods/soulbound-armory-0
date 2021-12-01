package soulboundarmory.mixin.access;

import java.util.List;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Matrix4f;

public interface FontRenderer$CharacterRendererAccess {
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

    List<TexturedGlyph.Effect> rectangles();

    IRenderTypeBuffer bufferSource();

    float invokeFinish(int underlineColor, float x);

    void invokeAddRectangle(TexturedGlyph.Effect rectangle);
}
