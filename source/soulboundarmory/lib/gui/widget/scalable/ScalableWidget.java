package soulboundarmory.lib.gui.widget.scalable;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import soulboundarmory.lib.gui.util.Rectangle;
import soulboundarmory.lib.gui.widget.Length;
import soulboundarmory.lib.gui.widget.Widget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;
import soulboundarmory.util.Util;

/**
 A textured widget that supports 9-slice scaling.
 */
@SuppressWarnings("unused")
public class ScalableWidget<T extends ScalableWidget<T>> extends Widget<T> {
    private static final Identifier widgetsID = new Identifier("textures/gui/advancements/widgets.png");
    private static final Identifier windowID = new Identifier("textures/gui/advancements/window.png");

    public final Rectangle[] middles = Util.fill(new Rectangle[5], Rectangle::new);
    public final Rectangle[] corners = Util.fill(new Rectangle[4], Rectangle::new);
    public final Rectangle border = new Rectangle();

    public AbstractTexture texture;

    public int u, v;

    public float r = 1;
    public float g = 1;
    public float b = 1;
    public float a = 1;

    protected int textureWidth = 256;
    protected int textureHeight = 256;

    protected Length viewWidth = new Length();
    protected Length viewHeight = new Length();

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

    /**
     Slice the texture into 9 parts: 4 non-scalable corners and 5 other resizable parts.
     <pre>
     - | + | -
     + | + | +
     - | + | -
     </pre>
     -: non-repeatable corner<br>
     +: repeatable non-corner

     @param u0 u coordinate just after a left corner
     @param u1 u coordinate just before a right corner
     @param u2 u coordinate just after a right corner
     @param v0 v coordinate just after a top corner
     @param v1 v coordinate just before a bottom corner
     @param v2 v coordinate just after a bottom corner
     @return {@code this}
     */
    public T slice(int u0, int u1, int u2, int v0, int v1, int v2) {
        var topLeft = this.corners[0];
        var topRight = this.corners[1];
        var bottomLeft = this.corners[2];
        var bottomRight = this.corners[3];
        bottomLeft.end.x = topLeft.end.x = u0;
        bottomRight.start.x = topRight.start.x = u1;
        bottomRight.end.x = topRight.end.x = u2;
        topRight.end.y = topLeft.end.y = v0;
        bottomRight.start.y = bottomLeft.start.y = v1;
        bottomRight.end.y = bottomLeft.end.y = v2;

        var top = this.middles[0];
        var left = this.middles[1];
        var center = this.middles[2];
        var right = this.middles[3];
        var bottom = this.middles[4];
        bottom.start.x = center.start.x = left.end.x = top.start.x = u0;
        bottom.end.x = right.start.x = center.end.x = top.end.x = u1;
        right.end.x = u2;
        right.start.y = center.start.y = left.start.y = top.end.y = v0;
        bottom.start.y = right.end.y = center.end.y = left.end.y = v1;
        bottom.end.y = v2;

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

    public T fullView() {
        return this.width(1F).height(1F);
    }

    public int viewWidth() {
        return this.viewWidth.get(this.width());
    }

    public int viewHeight() {
        return this.viewHeight.get(this.height());
    }

    public T viewWidth(int width) {
        this.viewWidth.set(width);

        return (T) this;
    }

    public T viewWidth(double width) {
        this.viewWidth.set(width);

        return (T) this;
    }

    public T viewHeight(int height) {
        this.viewHeight.set(height);

        return (T) this;
    }

    public T viewHeight(double height) {
        this.viewHeight.set(height);

        return (T) this;
    }

    public T view(int width, int height) {
        return this.viewWidth(width).viewHeight(height);
    }

    public T view(double width, double height) {
        return this.viewWidth(width).viewHeight(height);
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
        return this.texture(widgetsID).v(3 + index * 26).slice(5, 195, 200, 5, 15, 20);
    }

    public T rectangle(int index) {
        return this.texture(widgetsID).uv(1, 129 + index * 26).slice(6, 18, 24, 6, 18, 24);
    }

    public T spikedRectangle(int index) {
        return this.texture(widgetsID).uv(26, 128 + index * 26).slice(10, 16, 26, 10, 16, 26);
    }

    public T roundedRectangle(int index) {
        return this.texture(widgetsID).uv(54, 129 + index * 26).slice(7, 15, 22, 6, 21, 26);
    }

    public T button(int index) {
        return this.texture(ClickableWidget.WIDGETS_TEXTURE).v(46 + index * 20).slice(5, 195, 200, 5, 15, 20);
    }

    public T experienceBar() {
        return this.texture(GUI_ICONS_TEXTURE).v(64).slice(1, 138, 182, 1, 4, 5);
    }

    @Override
    protected void render() {
        shaderTexture(this.texture);
        this.resetColor();

        RenderSystem.enableBlend();
        this.renderCorners();
        this.renderMiddles();

        if (this.isFocused() && this.isActive()) {
            this.drawBorder();
        }
    }

    protected void renderCorners() {
        for (var index = 0; index < this.corners.length; ++index) {
            var corner = this.corners[index];
            var width = Math.max(0, this.viewWidth() + corner.width() - this.width());
            var height = Math.max(0, this.viewHeight() + corner.height() - this.height());

            if (width + height > 0) drawTexture(
                this.matrixes,
                this.x() + index % 2 * (this.width() - corner.width()),
                this.y() + index / 2 * (this.height() - corner.height()),
                this.z(),
                this.u + corner.start.x,
                this.v + corner.start.y,
                width,
                height,
                this.textureHeight(),
                this.textureWidth()
            );
        }
    }

    protected void renderMiddles() {
        shaderTexture(this.texture);
        var tessellator = Tessellator.getInstance();
        var buffer = tessellator.getBuffer();
        var matrix = this.matrixes.peek().getPositionMatrix();

        for (var index = 0; index < this.middles.length; ++index) {
            var middle = this.middles[index];
            var x = switch (index) {
                case 1 -> this.x();
                case 3 -> this.endX() - middle.width();
                default -> this.x() + this.middles[1].width();
            };
            var y = switch (index) {
                case 0 -> this.y();
                case 4 -> this.endY() - middle.height();
                default -> this.y() + this.middles[0].height();
            };
            var endX = switch (index) {
                case 1 -> this.x() + middle.width();
                case 3 -> this.endX();
                default -> this.endX() - this.middles[3].width();
            };
            var endY = switch (index) {
                case 0 -> this.y() + middle.height();
                case 4 -> this.endY();
                default -> this.endY() - this.middles[4].height();
            };

            var viewEndX = Math.min(endX, this.x() + this.viewWidth());
            var viewEndY = Math.min(endY, this.y() + this.viewHeight());

            if (viewEndX > x && viewEndY > y) {
                var textureWidth = (float) this.textureWidth();
                var textureHeight = (float) this.textureHeight();
                var u = (this.u + middle.start.x) / textureWidth;
                var v = (this.v + middle.start.y) / textureHeight;
                var endU = u + Math.min(viewEndX - x, middle.width()) / textureWidth;
                var endV = v + Math.min(viewEndY - y, middle.height()) / textureHeight;

                buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                buffer.vertex(matrix, x, viewEndY, this.z()).texture(u, endV).next();
                buffer.vertex(matrix, viewEndX, viewEndY, this.z()).texture(endU, endV).next();
                buffer.vertex(matrix, viewEndX, y, this.z()).texture(endU, v).next();
                buffer.vertex(matrix, x, y, this.z()).texture(u, v).next();
                tessellator.draw();
            }
        }
    }

    protected void drawBorder() {
        var endX = this.endX() - 1;
        var endY = this.endY();
        drawHorizontalLine(this.matrixes, this.x(), endX, this.y(), this.z(), -1);
        drawVerticalLine(this.matrixes, this.x(), this.y(), endY, this.z(), -1);
        drawVerticalLine(this.matrixes, endX, this.y(), endY, this.z(), -1);
        drawHorizontalLine(this.matrixes, this.x(), endX, endY - 1, this.z(), -1);
    }

    protected void resetColor() {
        if (this.isActive()) {
            RenderSystem.setShaderColor(this.r, this.g, this.b, this.a);
        } else {
            var chroma = 160F / 255;
            RenderSystem.setShaderColor(this.r * chroma, this.g * chroma, this.b * chroma, this.a);
        }
    }
}
