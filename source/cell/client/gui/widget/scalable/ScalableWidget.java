package cell.client.gui.widget.scalable;

import cell.client.gui.widget.Length;
import cell.client.gui.widget.Widget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;

/**
 A textured widget that supports 9-slice scaling.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ScalableWidget<T extends ScalableWidget<T>> extends Widget<T> {
    private static final Identifier widgetsID = new Identifier("textures/gui/advancements/widgets.png");
    private static final Identifier windowID = new Identifier("textures/gui/advancements/window.png");

    public final int[][][] middles = new int[5][4][2];
    public final int[][][] corners = new int[4][4][2];
    public final int[][] border = new int[4][2];

    public AbstractTexture texture;

    public int u, v;

    public float r = 1;
    public float g = 1;
    public float b = 1;
    public float a = 1;

    protected int textureWidth = 256;
    protected int textureHeight = 256;

    protected Length widthLimit = new Length();
    protected Length heightLimit = new Length();

    public T texture(AbstractTexture texture) {
        this.texture = texture;

        return (T) this;
    }

    public T texture(Identifier id) {
        var texture = textureManager.getTexture(id);

        if (texture == null) {
            texture = new ResourceTexture(id);
            textureManager.registerTexture(id, texture);
        }

        return this.texture(texture);
    }

    public T texture(String id) {
        return this.texture(new Identifier(id));
    }

    public T u(int u) {
        this.u = u;

        return (T) this;
    }

    public T v(int v) {
        this.v = v;

        return (T) this;
    }

    public T uv(int u, int v) {
        return this.u(u).v(v);
    }

    public T slice(int u0, int u1, int u2, int v0, int v1, int v2) {
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

        return (T) this;
    }

    public int textureWidth() {
        return this.textureWidth;
    }

    public int textureHeight() {
        return this.textureHeight;
    }

    public T textureWidth(int width) {
        this.textureWidth = width;

        return (T) this;
    }

    public T textureHeight(int height) {
        this.textureHeight = height;

        return (T) this;
    }

    public T textureSize(int width, int height) {
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
    public T width(int width) {
        return super.width(width);
    }

    @Override
    public T height(int height) {
        return super.height(height);
    }

    public T width(float width) {
        this.width.set(width);

        return (T) this;
    }

    public T height(float height) {
        this.height.set(height);

        return (T) this;
    }

    public T maxSize() {
        return this.width(1F).height(1F);
    }

    public int widthLimit() {
        return this.widthLimit.get(this.width());
    }

    public int heightLimit() {
        return this.heightLimit.get(this.height());
    }

    public T widthLimit(int width) {
        this.widthLimit.set(width);

        return (T) this;
    }

    public T widthLimit(double width) {
        this.widthLimit.set(width);

        return (T) this;
    }

    public T heightLimit(int height) {
        this.heightLimit.set(height);

        return (T) this;
    }

    public T heightLimit(double height) {
        this.heightLimit.set(height);

        return (T) this;
    }

    public T limit(int width, int height) {
        return this.widthLimit(width).heightLimit(height);
    }

    public T limit(double width, double height) {
        return this.widthLimit(width).heightLimit(height);
    }

    public T color4f(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;

        return (T) this;
    }

    public T color3f(float r, float g, float b) {
        return this.color4f(r, g, b, 1);
    }

    public T yellowRectangle() {
        return this.longRectangle(0);
    }

    public T blueRectangle() {
        return this.longRectangle(1);
    }

    public T grayRectangle() {
        return this.longRectangle(2);
    }

    public T yellowSpikedRectangle() {
        return this.spikedRectangle(0);
    }

    public T yellowRoundedRectangle() {
        return this.roundedRectangle(0);
    }

    public T whiteRectangle() {
        return this.rectangle(1);
    }

    public T whiteSpikedRectangle() {
        return this.spikedRectangle(1);
    }

    public T whiteRoundedRectangle() {
        return this.roundedRectangle(1);
    }

    public T slider() {
        return this.button(0);
    }

    public T button() {
        return this.button(1);
    }

    public T window() {
        return this.texture(windowID).slice(14, 238, 252, 22, 126, 140);
    }

    public T longRectangle(int index) {
        return this.texture(widgetsID).v(3 + index * 26).slice(2, 198, 200, 2, 18, 20);
    }

    public T rectangle(int index) {
        return this.texture(widgetsID).uv(1, 129 + index * 26).slice(2, 22, 24, 2, 22, 24);
    }

    public T spikedRectangle(int index) {
        return this.texture(widgetsID).uv(26, 128 + index * 26).slice(10, 16, 26, 10, 16, 26);
    }

    public T roundedRectangle(int index) {
        return this.texture(widgetsID).uv(54, 129 + index * 26).slice(7, 15, 22, 4, 21, 26);
    }

    public T button(int index) {
        return this.texture(ClickableWidget.WIDGETS_TEXTURE).v(46 + index * 20).slice(2, 198, 200, 2, 17, 20);
    }

    public T experienceBar() {
        return this.texture(GUI_ICONS_TEXTURE).v(64).slice(1, 172, 182, 1, 4, 5);
    }

    @Override
    public void render() {
        shaderTexture(this.texture);
        this.resetColor();

        RenderSystem.enableBlend();
        this.renderCorners();
        this.renderMiddles();
        // this.renderAll();
        RenderSystem.disableBlend();

        if (this.focused() && this.active()) {
            this.drawBorder();
        }
    }

    protected void renderCorners() {
        var corners = this.corners;

        for (int i = 0, length = corners.length; i < length; ++i) {
            var corner = corners[i];
            var topLeft = corner[0];
            var u = topLeft[0];
            var v = topLeft[1];
            var width = corner[1][0] - u;
            var height = corner[2][1] - v;

            drawTexture(this.matrixes, this.x() + i % 2 * (this.width() - width), this.y() + i / 2 * (this.height() - height), this.z(), this.u + u, this.v + v, width, height, this.textureHeight(), this.textureWidth());
        }
    }

    protected void renderMiddles() {
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

                    drawTexture(this.matrixes, endX - remainingWidth, y, this.z(), absoluteU, absoluteV, drawnWidth, drawnHeight, this.textureHeight(), this.textureWidth());
                }
            }
        }
    }

    protected void renderAll() {
        int[][][] sections = {};

        for (var index = 0; index < sections.length; index++) {
            var section = sections[index];
        }
    }

    protected void drawBorder() {
        var endX = this.x() + this.width() - 1;
        var endY = this.y() + this.height();

        drawHorizontalLine(this.matrixes, this.x(), endX, this.y(), this.z(), -1);
        drawVerticalLine(this.matrixes, this.x(), this.y(), endY, this.z(), -1);
        drawVerticalLine(this.matrixes, endX, this.y(), endY, this.z(), -1);
        drawHorizontalLine(this.matrixes, this.x(), endX, endY - 1, this.z(), -1);
    }

    protected void detectBorder() {}

    protected void resetColor() {
        if (this.active()) {
            RenderSystem.setShaderColor(this.r, this.g, this.b, this.a);
        } else {
            var chroma = 160F / 255;
            RenderSystem.setShaderColor(this.r * chroma, this.g * chroma, this.b * chroma, this.a);
        }
    }
}
