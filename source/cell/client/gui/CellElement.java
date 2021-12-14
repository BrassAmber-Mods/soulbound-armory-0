package cell.client.gui;

import cell.client.gui.screen.CellScreen;
import cell.client.gui.screen.ScreenDelegate;
import cell.client.gui.widget.Coordinate;
import cell.client.gui.widget.Length;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.opengl.GL11;

public abstract class CellElement<T extends CellElement<T>> extends DrawableHelper implements DrawableElement, Cloneable {
    public Coordinate x = new Coordinate();
    public Coordinate y = new Coordinate();

    protected Length width = new Length();
    protected Length height = new Length();

    public static Screen screen() {
        return minecraft.currentScreen;
    }

    public static CellScreen<?> cellScreen() {
        return minecraft.currentScreen instanceof ScreenDelegate screen ? screen.screen : null;
    }

    public static int fontHeight() {
        return textDrawer.fontHeight;
    }

    public static boolean isShiftDown() {
        return Screen.hasShiftDown();
    }

    public static boolean isControlDown() {
        return Screen.hasControlDown();
    }

    public static boolean isAltDown() {
        return Screen.hasAltDown();
    }

    public static float tickDelta() {
        return Animation.getPartialTickTime();
    }

    public static double mouseX() {
        return mouse.getX() * (double) window.getScaledWidth() / window.getWidth();
    }

    public static double mouseY() {
        return mouse.getY() * (double) window.getScaledHeight() / window.getHeight();
    }

    /**
     @return whether an area starting at (`startX`, `startY`) with dimensions (`width`, `height`) contains the point (`x`, `y`).
     */
    public static boolean contains(double x, double y, double startX, double startY, double width, double height) {
        return x >= startX && x <= startX + width && y >= startY && y <= startY + height;
    }

    public static boolean isPressed(int keyCode) {
        return InputUtil.isKeyPressed(minecraft.getWindow().getHandle(), keyCode);
    }

    public static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, float z, int color) {
        fill(matrices.peek().getModel(), x1, y1, x2, y2, z, color);
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

        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x1, y2, z).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, x2, y2, z).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, x2, y1, z).color(r, g, b, a).next();
        bufferBuilder.vertex(matrix, x1, y1, z).color(r, g, b, a).next();
        bufferBuilder.end();

        BufferRenderer.draw(bufferBuilder);
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

    /**
     Draw text with stroke.

     @param text the text
     @param x the x at which to start
     @param y the y at which to start
     @param color the color of the text
     @param strokeColor the color of the stroke
     */
    public static void drawStrokedText(MatrixStack matrixes, Text text, float x, float y, int color, int strokeColor) {
        textDrawer.draw(matrixes, text, x + 1, y, strokeColor);
        textDrawer.draw(matrixes, text, x - 1, y, strokeColor);
        textDrawer.draw(matrixes, text, x, y + 1, strokeColor);
        textDrawer.draw(matrixes, text, x, y - 1, strokeColor);
        textDrawer.draw(matrixes, text, x, y, color);
    }

    /**
     Draw text with stroke.

     @param text the text
     @param x the x at which to start
     @param y the y at which to start
     @param color the color of the text
     @param strokeColor the color of the stroke
     */
    public static void drawStrokedText(MatrixStack matrixes, String string, float x, float y, int color, int strokeColor) {
        textDrawer.draw(matrixes, string, x + 1, y, strokeColor);
        textDrawer.draw(matrixes, string, x - 1, y, strokeColor);
        textDrawer.draw(matrixes, string, x, y + 1, strokeColor);
        textDrawer.draw(matrixes, string, x, y - 1, strokeColor);
        textDrawer.draw(matrixes, string, x, y, color);
    }

    /**
     Draw text with stroke.

     @param text the text
     @param x the x at which to start
     @param y the y at which to start
     @param color the color of the text
     */
    public static void drawStrokedText(MatrixStack matrixes, Text text, float x, float y, int color) {
        drawStrokedText(matrixes, text, x, y, color, 0);
    }

    /**
     Draw text with stroke.

     @param text the text
     @param x the x at which to start
     @param y the y at which to start
     @param color the color of the text
     */
    public static void drawStrokedText(MatrixStack matrixes, String text, float x, float y, int color) {
        drawStrokedText(matrixes, text, x, y, color, 0);
    }

    public static void renderTooltip(MatrixStack matrixes, List<? extends StringVisitable> lines, double x, double y, int maxTextWidth) {
        GuiUtils.drawHoveringText(matrixes, lines, (int) x, (int) y, window.getScaledWidth(), window.getScaledHeight(), maxTextWidth, textDrawer);
    }

    public static void renderTooltip(MatrixStack matrixes, List<? extends StringVisitable> lines, double x, double y) {
        GuiUtils.drawHoveringText(matrixes, lines, (int) x, (int) y, window.getScaledWidth(), window.getScaledHeight(), -1, textDrawer);
    }

    public static void renderTooltip(MatrixStack matrixes, StringVisitable text, double x, double y, int maxTextWidth) {
        GuiUtils.drawHoveringText(matrixes, List.of(text), (int) x, (int) y, window.getScaledWidth(), window.getScaledHeight(), maxTextWidth, textDrawer);
    }

    public static void renderTooltip(MatrixStack matrixes, StringVisitable text, double x, double y) {
        GuiUtils.drawHoveringText(matrixes, List.of(text), (int) x, (int) y, window.getScaledWidth(), window.getScaledHeight(), -1, textDrawer);
    }

    public static void renderBackground(Identifier identifier, int x, int y, int width, int height, int chroma, int alpha) {
        var tessellator = Tessellator.getInstance();
        var builder = tessellator.getBuffer();
        float f = 1 << 5;
        float endX = x + width;
        float endY = y + height;

        textureManager.bindTexture(identifier);
        RenderSystem.color4f(1, 1, 1, 1);

        builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        builder.vertex(x, endY, 0).color(chroma, chroma, chroma, 255).texture(0, endY / f + alpha).next();
        builder.vertex(endX, endY, 0).color(chroma, chroma, chroma, 255).texture(endX / f, endY / f + alpha).next();
        builder.vertex(endX, y, 0).color(chroma, chroma, chroma, 255).texture(endX / f, alpha).next();
        builder.vertex(x, y, 0).color(chroma, chroma, chroma, 255).texture(0, alpha).next();

        tessellator.draw();
    }

    public static void renderBackground(Identifier identifier, int x, int y, int width, int height, int chroma) {
        renderBackground(identifier, x, y, width, height, chroma, 0);
    }

    public static void renderBackground(Identifier identifier, int x, int y, int width, int height) {
        renderBackground(identifier, x, y, width, height, 64, 0);
    }

    public static void renderBackground(MatrixStack matrixes) {
        screen().renderBackground(matrixes);
    }

    public static List<StringVisitable> wrap(List<? extends StringVisitable> lines, int width) {
        return lines.stream().map(line -> textHandler.wrapLines(line, width, Style.EMPTY)).flatMap(List::stream).toList();
    }

    public int x() {
        return this.x.get(0, 0);
    }

    public T x(int x) {
        this.x.set(x);

        return (T) this;
    }

    public int y() {
        return this.y.get(0, 0);
    }

    public T y(int y) {
        this.y.set(y);

        return (T) this;
    }

    public int z() {
        return this.getZOffset();
    }

    public T z(int z) {
        this.setZOffset(z);

        return (T) this;
    }

    public T addZ(int z) {
        return this.z(this.z() + z);
    }

    public void withZ(int z, Runnable runnable) {
        this.addZ(z);
        itemRenderer.zOffset = this.z();
        runnable.run();
        this.addZ(-z);
        itemRenderer.zOffset = this.z();
    }

    public void renderGuiItem(ItemStack itemStack, int x, int y, int z) {
        this.withZ(z, () -> itemRenderer.renderGuiItemIcon(itemStack, x, y));
    }

    public int width() {
        return this.width.get();
    }

    public T width(int width) {
        this.width.set(width);

        return (T) this;
    }

    public int height() {
        return this.height.get();
    }

    public T height(int height) {
        this.height.set(height);

        return (T) this;
    }

    public boolean contains(double x, double y) {
        return contains(x, y, this.x(), this.y(), this.width(), this.height());
    }

    @Override
    protected T clone() {
        try {
            return (T) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw Unsafe.throwException(exception);
        }
    }
}
