package soulboundarmory.lib.gui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import com.mojang.blaze3d.systems.RenderSystem;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import soulboundarmory.function.BiFloatIntConsumer;
import soulboundarmory.lib.gui.coordinate.Coordinate;
import soulboundarmory.lib.gui.screen.CellScreen;
import soulboundarmory.lib.gui.screen.ScreenDelegate;
import soulboundarmory.lib.gui.widget.Length;

public abstract class AbstractNode<B extends AbstractNode<B, ?>, T extends AbstractNode<B, T>> extends DrawableHelper implements Node<B, T>, Cloneable {
    public Coordinate x = new Coordinate();
    public Coordinate y = new Coordinate();

    protected Length width = new Length();
    protected Length height = new Length();

    public static Screen screen() {
        return client.currentScreen;
    }

    public static CellScreen<?> cellScreen() {
        return client.currentScreen instanceof ScreenDelegate screen ? screen.screen : null;
    }

    public static int fontHeight() {
        return textRenderer.fontHeight;
    }

    public static int windowWidth() {
        return window.getScaledWidth();
    }

    public static int windowHeight() {
        return window.getScaledHeight();
    }

    public static int unscale(int scaledX) {
        return (int) (scaledX * window.getScaleFactor());
    }

    public static float tickDelta() {
        return client.getLastFrameDuration();
    }

    public static ClientPlayerEntity player() {
        return client.player;
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

    public static double mouseX() {
        return mouse.getX() * window.getScaledWidth() / window.getWidth();
    }

    public static double mouseY() {
        return mouse.getY() * window.getScaledHeight() / window.getHeight();
    }

    public static int width(String string) {
        return textRenderer.getWidth(string);
    }

    public static int width(StringVisitable text) {
        return textRenderer.getWidth(text);
    }

    public static int width(OrderedText text) {
        return textRenderer.getWidth(text);
    }

    public static int width(Stream<? extends StringVisitable> text) {
        return text.map(AbstractNode::width).max(Comparator.naturalOrder()).orElse(0);
    }

    /**
     @return whether an area starting at (`startX`, `startY`) with dimensions (`width`, `height`) contains the point (`x`, `y`)
     */
    public static boolean contains(double x, double y, double startX, double startY, double width, double height) {
        return x >= startX && x <= startX + width && y >= startY && y <= startY + height;
    }

    public static boolean isPressed(int keyCode) {
        return InputUtil.isKeyPressed(client.getWindow().getHandle(), keyCode);
    }

    public static void shaderTexture(AbstractTexture texture) {
        RenderSystem.setShaderTexture(0, texture.getGlId());
    }

    public static void shaderTexture(Identifier texture) {
        RenderSystem.setShaderTexture(0, texture);
    }

    public static void bind(Identifier texture) {
        textureManager.bindTexture(texture);
    }

    public static void setPositionColorShader() {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
    }

    public static void setPositionColorTextureShader() {
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
    }

    public static void chroma(float chroma) {
        RenderSystem.setShaderColor(chroma, chroma, chroma, -1);
    }

    public static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, float z, int color) {
        fill(matrices.peek().getPositionMatrix(), x1, y1, x2, y2, z, color);
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

        setPositionColorShader();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
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

    public static void stroke(float x, float y, int color, int strokeColor, BiFloatIntConsumer draw) {
        draw.accept(x + 1, y, strokeColor);
        draw.accept(x - 1, y, strokeColor);
        draw.accept(x, y + 1, strokeColor);
        draw.accept(x, y - 1, strokeColor);
        draw.accept(x, y, color);
    }

    public static void stroke(float x, float y, int color, BiFloatIntConsumer draw) {
        stroke(x, y, color, 0, draw);
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
        stroke(x, y, color, strokeColor, (i, j, color1) -> textRenderer.draw(matrixes, text, i, j, color1));
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
        stroke(x, y, color, strokeColor, (i, j, color1) -> textRenderer.draw(matrixes, string, i, j, color1));
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

    public static void renderTooltip(MatrixStack matrixes, List<? extends StringVisitable> lines, double x, double y) {
        screen().renderComponentTooltip(matrixes, lines, (int) x, (int) y, textRenderer, ItemStack.EMPTY);
    }

    public static void renderTooltip(MatrixStack matrixes, StringVisitable text, double x, double y) {
        renderTooltip(matrixes, List.of(text), (int) x, (int) y);
    }

    public static void renderTooltipFromComponents(MatrixStack matrixes, List<? extends TooltipComponent> components, double x, double y) {
        screen().renderTooltipFromComponents(matrixes, (List<TooltipComponent>) components, (int) x, (int) y);
    }

    public static List<StringVisitable> wrap(List<? extends StringVisitable> lines, int width) {
        return lines.stream().map(line -> textHandler.wrapLines(line, width, Style.EMPTY)).flatMap(List::stream).toList();
    }

    public void renderBackground(Identifier identifier, int x, int y, int width, int height, int chroma, int alpha) {
        var tessellator = Tessellator.getInstance();
        var buffer = tessellator.getBuffer();
        float f = 32;
        float endX = x + width;
        float endY = y + height;

        shaderTexture(identifier);
        setPositionColorTextureShader();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        buffer.vertex(x, endY, this.z() - 1000).color(chroma, chroma, chroma, 255).texture(0, endY / f + alpha).next();
        buffer.vertex(endX, endY, this.z() - 1000).color(chroma, chroma, chroma, 255).texture(endX / f, endY / f + alpha).next();
        buffer.vertex(endX, y, this.z() - 1000).color(chroma, chroma, chroma, 255).texture(endX / f, alpha).next();
        buffer.vertex(x, y, this.z() - 1000).color(chroma, chroma, chroma, 255).texture(0, alpha).next();
        tessellator.draw();
    }

    public void renderBackground(Identifier identifier, int x, int y, int width, int height, int chroma) {
        this.renderBackground(identifier, x, y, width, height, chroma, 0);
    }

    public void renderBackground(Identifier identifier, int x, int y, int width, int height) {
        this.renderBackground(identifier, x, y, width, height, 64, 0);
    }

    public void renderBackground(MatrixStack matrixes) {
        screen().renderBackground(matrixes);
    }

    @Override
    public int x() {
        return this.x.resolve(0, 0, 0);
    }

    public T x(int x) {
        this.x.set(x);

        return (T) this;
    }

    @Override
    public int y() {
        return this.y.resolve(0, 0, 0);
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

    public void withZ(Runnable runnable) {
        var previousZ = itemRenderer.zOffset;
        itemRenderer.zOffset = this.z();
        runnable.run();
        itemRenderer.zOffset = previousZ;
    }

    public void renderGuiItem(ItemStack itemStack, int x, int y) {
        this.withZ(() -> itemRenderer.renderGuiItemIcon(itemStack, x, y));
    }

    @Override
    public int width() {
        return this.width.get();
    }

    public T width(int width) {
        this.width.set(width);

        return (T) this;
    }

    @Override
    public int height() {
        return this.height.get();
    }

    public T height(int height) {
        this.height.set(height);

        return (T) this;
    }

    public T size(int size) {
        return this.width(size).height(size);
    }

    @Override
    public T clone() {
        try {
            return (T) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw Unsafe.throwException(exception);
        }
    }
}
