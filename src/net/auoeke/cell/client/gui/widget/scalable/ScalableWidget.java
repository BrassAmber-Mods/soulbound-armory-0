package net.auoeke.cell.client.gui.widget.scalable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.auoeke.cell.client.gui.CellElement;
import net.auoeke.cell.client.gui.DrawableElement;
import net.auoeke.cell.client.gui.widget.Length;
import net.auoeke.cell.client.gui.widget.Widget;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings({"UnusedReturnValue", "unused"})
@OnlyIn(Dist.CLIENT)
public class ScalableWidget extends Widget<ScalableWidget> {
    private static final ResourceLocation advancementWidgets = new ResourceLocation("textures/gui/advancements/widgets.png");
    private static final ResourceLocation window = new ResourceLocation("textures/gui/advancements/window.png");

    public final int[][][] middles = new int[5][4][2];
    public final int[][][] corners = new int[4][4][2];
    public final int[][] border = new int[4][2];

    public Texture texture;

    public int u, v;

    public float r = 1;
    public float g = 1;
    public float b = 1;
    public float a = 1;

    protected int textureWidth = 256;
    protected int textureHeight = 256;

    protected Length widthLimit = new Length();
    protected Length heightLimit = new Length();

    public ScalableWidget texture(Texture texture) {
        this.texture = texture;

        return this;
    }

    public ScalableWidget texture(ResourceLocation id) {
        var texture = DrawableElement.textureManager.getTexture(id);

        if (texture == null) {
            texture = new SimpleTexture(id);
            DrawableElement.textureManager.register(id, texture);
        }

        return this.texture(texture);
    }

    public ScalableWidget texture(String id) {
        return this.texture(new ResourceLocation(id));
    }

    public ScalableWidget u(int u) {
        this.u = u;

        return this;
    }

    public ScalableWidget v(int v) {
        this.v = v;

        return this;
    }

    public ScalableWidget uv(int u, int v) {
        return this.u(u).v(v);
    }

    public ScalableWidget slice(int u0, int u1, int u2, int v0, int v1, int v2) {
        this.corners[2][3][0] = this.corners[2][1][0] = this.corners[0][3][0] = this.corners[0][1][0] = u0;
        this.corners[3][2][0] = this.corners[3][0][0] = this.corners[1][2][0] = this.corners[1][0][0] = u1;
        this.corners[3][3][0] = this.corners[3][1][0] = this.corners[1][3][0] = this.corners[1][1][0] = u2;
        this.corners[1][3][1] = this.corners[1][2][1] = this.corners[0][3][1] = this.corners[0][2][1] = v0;
        this.corners[3][1][1] = this.corners[3][0][1] = this.corners[2][1][1] = this.corners[2][0][1] = v1;
        this.corners[3][3][1] = this.corners[3][2][1] = this.corners[2][3][1] = this.corners[2][2][1] = v2;

        this.middles[4][2][0] = this.middles[4][0][0] = this.middles[2][2][0] = this.middles[2][0][0] = this.middles[1][3][0] = this.middles[1][1][0] = this.middles[0][2][0] = this.middles[0][0][0] = u0;
        this.middles[4][3][0] = this.middles[4][1][0] = this.middles[3][2][0] = this.middles[3][0][0] = this.middles[2][3][0] = this.middles[2][1][0] = this.middles[0][3][0] = this.middles[0][1][0] = u1;
        this.middles[3][3][0] = this.middles[3][1][0] = u2;
        this.middles[3][1][1] = this.middles[3][0][1] = this.middles[2][1][1] = this.middles[2][0][1] = this.middles[1][1][1] = this.middles[1][0][1] = this.middles[0][3][1] = this.middles[0][2][1] = v0;
        this.middles[4][1][1] = this.middles[4][0][1] = this.middles[3][3][1] = this.middles[3][2][1] = this.middles[2][3][1] = this.middles[2][2][1] = this.middles[1][3][1] = this.middles[1][2][1] = v1;
        this.middles[4][3][1] = this.middles[4][2][1] = v2;

        return this;
    }

    public int textureWidth() {
        return this.textureWidth;
    }

    public int textureHeight() {
        return this.textureHeight;
    }

    public ScalableWidget textureWidth(int width) {
        this.textureWidth = width;

        return this;
    }

    public ScalableWidget textureHeight(int height) {
        this.textureHeight = height;

        return this;
    }

    public ScalableWidget textureSize(int width, int height) {
        return this.textureWidth(width).textureHeight(height);
    }

    @Override
    public int width() {
        return this.width.get(this.textureWidth);
    }

    @Override
    public int height() {
        return this.height.get(this.textureHeight);
    }

    @Override
    public ScalableWidget width(int width) {
        return super.width(width);
    }

    @Override
    public ScalableWidget height(int height) {
        return super.height(height);
    }

    public ScalableWidget width(float width) {
        this.width.set(width);

        return this;
    }

    public ScalableWidget height(float height) {
        this.height.set(height);

        return this;
    }

    public ScalableWidget max() {
        return this.width(1F).height(1F);
    }

    public int widthLimit() {
        return this.widthLimit.get(this.width());
    }

    public int heightLimit() {
        return this.heightLimit.get(this.height());
    }

    public ScalableWidget widthLimit(int width) {
        this.widthLimit.set(width);

        return this;
    }

    public ScalableWidget widthLimit(float width) {
        this.widthLimit.set(width);

        return this;
    }

    public ScalableWidget heightLimit(int height) {
        this.heightLimit.set(height);

        return this;
    }

    public ScalableWidget heightLimit(float height) {
        this.heightLimit.set(height);

        return this;
    }

    public ScalableWidget limit(int width, int height) {
        return this.widthLimit(width).heightLimit(height);
    }

    public ScalableWidget limit(float width, float height) {
        return this.widthLimit(width).heightLimit(height);
    }

    public ScalableWidget color4f(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;

        return this;
    }

    public ScalableWidget color3f(float r, float g, float b) {
        return this.color4f(r, g, b, 1);
    }

    public ScalableWidget yellowRectangle() {
        return this.longRectangle(0);
    }

    public ScalableWidget blueRectangle() {
        return this.longRectangle(1);
    }

    public ScalableWidget grayRectangle() {
        return this.longRectangle(2);
    }

    public ScalableWidget yellowSpikedRectangle() {
        return this.spikedRectangle(0);
    }

    public ScalableWidget yellowRoundedRectangle() {
        return this.roundedRectangle(0);
    }

    public ScalableWidget whiteRectangle() {
        return this.rectangle(1);
    }

    public ScalableWidget whiteSpikedRectangle() {
        return this.spikedRectangle(1);
    }

    public ScalableWidget whiteRoundedRectangle() {
        return this.roundedRectangle(1);
    }

    public ScalableWidget inactiveButton() {
        return this.button(0);
    }

    public ScalableWidget button() {
        return this.button(1);
    }

    public ScalableWidget window() {
        return this.texture(window).slice(14, 238, 252, 22, 126, 140);
    }

    public ScalableWidget longRectangle(int index) {
        return this.texture(advancementWidgets).v(3 + index * 26).slice(2, 198, 200, 2, 18, 20);
    }

    public ScalableWidget rectangle(int index) {
        return this.texture(advancementWidgets).uv(1, 129 + index * 26).slice(2, 22, 24, 2, 22, 24);
    }

    public ScalableWidget spikedRectangle(int index) {
        return this.texture(advancementWidgets).uv(26, 128 + index * 26).slice(10, 16, 26, 10, 16, 26);
    }

    public ScalableWidget roundedRectangle(int index) {
        return this.texture(advancementWidgets).uv(54, 129 + index * 26).slice(7, 15, 22, 4, 21, 26);
    }

    public ScalableWidget button(int index) {
        return this.texture(net.minecraft.client.gui.widget.Widget.WIDGETS_LOCATION).v(46 + index * 20).slice(2, 198, 200, 2, 17, 20);
    }

    public ScalableWidget experienceBar() {
        return this.texture(GUI_ICONS_LOCATION).v(64).slice(1, 172, 182, 1, 4, 5);
    }

    @Override
    public void renderBackground(MatrixStack matrixes, int mouseX, int mouseY, float delta) {
        this.texture.bind();
        this.resetColor();

        RenderSystem.enableBlend();
        this.renderCorners(matrixes);
        this.renderMiddles(matrixes);
        // this.renderAll(matrixes);
        RenderSystem.disableBlend();

        if (this.focused && this.active && (this.primaryAction != null || this.secondaryAction != null)) {
            this.drawBorder(matrixes);
        }
    }

    protected void renderCorners(MatrixStack matrices) {
        var corners = this.corners;

        for (int i = 0, length = corners.length; i < length; ++i) {
            var corner = corners[i];
            var topLeft = corner[0];
            var u = topLeft[0];
            var v = topLeft[1];
            var width = corner[1][0] - u;
            var height = corner[2][1] - v;

            blit(matrices, this.x() + i % 2 * (this.width() - width), this.y() + i / 2 * (this.height() - height), this.getBlitOffset(), this.u + u, this.v + v, width, height, this.textureHeight(), this.textureWidth());
        }
    }

    protected void renderMiddles(MatrixStack matrices) {
        var middles = this.middles;
        var corners = this.corners;
        var middleWidth = this.widthLimit() - corners[0][1][0] + corners[1][0][0] - corners[1][1][0];
        var middleHeight = this.heightLimit() - corners[0][2][1] + corners[2][0][1] - corners[2][2][1];

        if (middleWidth > 0) for (int i = 0, length = middles.length; i < length; ++i) {
            var middle = middles[i];
            var u = middle[0][0];
            var v = middle[0][1];
            var endU = middle[1][0];
            var endV = middle[2][1];
            var maxWidth = endU - u;
            var maxHeight = endV - v;
            var absoluteU = this.u + u;
            var absoluteV = this.v + v;
            var remainingHeight = (i & 3) == 0 ? maxHeight : middleHeight;
            var endX = (i & 1) == 0
                ? this.x() + middle[0][0] + middleWidth
                : (i == 1 ? this.x() + middle[1][0] : this.x() + middles[1][1][0] + middleWidth + middle[1][0] - middle[0][0]);
            var endY = (i & 3) == 0
                ? (i == 0 ? this.y() + middle[2][1] : this.y() + middles[0][2][1] + middleHeight + middle[2][1] - middle[0][1])
                : this.y() + middle[0][1] + middleHeight;

            for (int drawnHeight; remainingHeight > 0; remainingHeight -= drawnHeight) {
                var remainingWidth = (i & 1) == 0 ? middleWidth : maxWidth;
                var y = endY - remainingHeight;
                drawnHeight = Math.min(remainingHeight, maxHeight);

                for (int drawnWidth; remainingWidth > 0; remainingWidth -= drawnWidth) {
                    drawnWidth = Math.min(remainingWidth, maxWidth);

                    blit(matrices, endX - remainingWidth, y, this.getBlitOffset(), absoluteU, absoluteV, drawnWidth, drawnHeight, this.textureHeight(), this.textureWidth());
                }
            }
        }
    }

    protected void renderAll(MatrixStack matrixes) {
        int[][][] sections = {};

        for (var index = 0; index < sections.length; index++) {
            var section = sections[index];
        }
    }

    protected void drawBorder(MatrixStack matrices) {
        var endX = this.x() + this.width() - 1;
        var endY = this.y() + this.height();

        CellElement.drawHorizontalLine(matrices, this.x(), endX, this.y(), this.getBlitOffset(), -1);
        CellElement.drawVerticalLine(matrices, this.x(), this.y(), endY, this.getBlitOffset(), -1);
        CellElement.drawVerticalLine(matrices, endX, this.y(), endY, this.getBlitOffset(), -1);
        CellElement.drawHorizontalLine(matrices, this.x(), endX, endY - 1, this.getBlitOffset(), -1);
    }

    protected void detectBorder() {}

    protected void resetColor() {
        if (this.active) {
            RenderSystem.color4f(this.r, this.g, this.b, this.a);
        } else {
            var chroma = 160F / 255;

            RenderSystem.color4f(this.r * chroma, this.g * chroma, this.b * chroma, this.a);
        }
    }
}
