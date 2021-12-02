package cell.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cell.client.gui.widget.Length;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public abstract class CellElement extends AbstractGui implements DrawableElement, Cloneable {
    public int x;
    public int y;

    protected Length width = new Length();
    protected Length height = new Length();

    public static boolean contains(double x, double y, double startX, double startY, double width, double height) {
        return x >= startX && x <= startX + width && y >= startY && y <= startY + height;
    }

    public static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, float z, int color) {
        fill(matrices.getLast().getMatrix(), x1, y1, x2, y2, z, color);
    }

    public static void fill(Matrix4f matrix, int x1, int y1, int x2, int y2, float z, int color) {
        int i;

        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        var a = (color >> 24 & 255) / 255F;
        var r = (color >> 16 & 255) / 255F;
        var g = (color >> 8 & 255) / 255F;
        var b = (color & 255) / 255F;
        var bufferBuilder = Tessellator.getInstance().getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();

        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferBuilder.pos(matrix, x1, y2, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y1, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x1, y1, z).color(r, g, b, a).endVertex();
        bufferBuilder.finishDrawing();

        WorldVertexBufferUploader.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawHorizontalLine(MatrixStack matrices, int x1, int x2, int y, int z, int color) {
        if (x2 < x1) {
            var i = x1;

            x1 = x2;
            x2 = i;
        }

        fill(matrices, x1, y, x2 + 1, y + 1, z, color);
    }

    public static void drawVerticalLine(MatrixStack matrices, int x, int y1, int y2, int z, int color) {
        if (y2 < y1) {
            var i = y1;

            y1 = y2;
            y2 = i;
        }

        fill(matrices, x, y1 + 1, x + 1, y2, z, color);
    }

    @Override
    protected CellElement clone() {
        try {
            return (CellElement) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception);
        }
    }
}
