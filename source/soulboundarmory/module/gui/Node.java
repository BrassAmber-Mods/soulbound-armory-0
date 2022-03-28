package soulboundarmory.module.gui;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulboundarmory.function.BiFloatIntConsumer;
import soulboundarmory.module.gui.coordinate.Coordinate;
import soulboundarmory.module.gui.screen.ScreenDelegate;
import soulboundarmory.module.gui.screen.ScreenWidget;
import soulboundarmory.module.gui.widget.Widget;
import soulboundarmory.util.Util;

/**
 A node in a tree of GUI elements.

 @param <B> the base type wherewith this node can interact (have as parents or children)
 @param <T> the type of the node
 */
@OnlyIn(Dist.CLIENT)
public abstract class Node<B extends Node<B, ?>, T extends Node<B, T>> extends DrawableHelper implements Drawable, Element, Cloneable {
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final Window window = client.getWindow();
    public static final Keyboard keyboard = client.keyboard;
    public static final Mouse mouse = client.mouse;
    public static final TextRenderer textRenderer = client.textRenderer;
    public static final TextureManager textureManager = client.textureManager;
    public static final TextHandler textHandler = textRenderer.getTextHandler();
    public static final ResourceManager resourceManager = client.getResourceManager();
    public static final ItemRenderer itemRenderer = client.getItemRenderer();
    public static final EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderDispatcher();
    public static final InGameHud hud = client.inGameHud;
    public static final BakedModelManager bakedModelManager = client.getBakedModelManager();
    public static final GameRenderer gameRenderer = client.gameRenderer;
    public static final SoundManager soundManager = client.getSoundManager();

    protected Coordinate x = new Coordinate();
    protected Coordinate y = new Coordinate();
    protected Length width = new Length();
    protected Length height = new Length();

    public static Screen screen() {
        return client.currentScreen;
    }

    public static ScreenWidget<?> cellScreen() {
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

    public static int unscale(int scaled) {
        return (int) (scaled * window.getScaleFactor());
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

    public static int width(Stream<? extends StringVisitable> text) {
        return text.mapToInt(Widget::width).max().orElse(0);
    }

    public static int width(Iterable<? extends StringVisitable> text) {
        return width(Util.stream(text));
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

    @Override
    public T clone() {
        return (T) super.clone();
    }

    /**
     @return this node's current x coordinate
     */
    public int absoluteX() {
        return this.x.resolve(Util.zeroSupplier, Util.zeroSupplier, Util.zeroSupplier);
    }

    public T x(int x) {
        this.x.set(x);

        return (T) this;
    }

    public T x(ToIntFunction<T> x) {
        this.x.set(() -> x.applyAsInt((T) this));

        return (T) this;
    }

    /**
     @return the x coordinate of this node's center
     */
    public int absoluteMiddleX() {
        return this.absoluteX() + this.width() / 2;
    }

    /**
     @return the x coordinate at the end of this node
     */
    public int absoluteEndX() {
        return this.absoluteX() + this.width();
    }

    /**
     @return this node's current y coordinate
     */
    public int absoluteY() {
        return this.y.resolve(Util.zeroSupplier, Util.zeroSupplier, Util.zeroSupplier);
    }

    public T y(int y) {
        this.y.set(y);

        return (T) this;
    }

    public T y(ToIntFunction<T> y) {
        this.y.set(() -> y.applyAsInt((T) this));

        return (T) this;
    }

    /**
     @return the y coordinate of this node's center
     */
    public int absoluteMiddleY() {
        return this.absoluteY() + this.height() / 2;
    }

    /**
     @return the y coordinate at the end of this node
     */
    public int absoluteEndY() {
        return this.absoluteY() + this.height();
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

    /**
     @return this node's current width
     */
    public int width() {
        return this.width.value.getAsInt() + (int) switch (this.width.type) {
            case EXACT -> this.width.base().getAsDouble();
            case PARENT_PROPORTION -> this.width.base().getAsDouble() * this.parent().map(B::width).orElse(1);
            case CHILD_SUM -> this.descendantWidth();
        };
    }

    public T width(int width) {
        this.width.base(width);

        return (T) this;
    }

    public T width(double width) {
        this.width.base(width);

        return (T) this;
    }

    public T width(ToIntFunction<T> width) {
        this.width.base(() -> width.applyAsInt((T) this));

        return (T) this;
    }

    public T widthProportion(ToDoubleFunction<T> width) {
        this.width.base(() -> width.applyAsDouble((T) this));

        return (T) this;
    }

    /**
     @return this node's current height
     */
    public int height() {
        return this.height.value.getAsInt() + (int) switch (this.height.type) {
            case EXACT -> this.height.base().getAsDouble();
            case PARENT_PROPORTION -> this.height.base().getAsDouble() * this.parent().map(B::height).orElse(1);
            case CHILD_SUM -> this.descendantHeight();
        };
    }

    public T height(int height) {
        this.height.base(height);

        return (T) this;
    }

    public T height(double height) {
        this.height.base(height);

        return (T) this;
    }

    public T height(ToIntFunction<T> height) {
        this.height.base(() -> height.applyAsInt((T) this));

        return (T) this;
    }

    public T heightProportion(ToDoubleFunction<T> height) {
        this.height.base(() -> height.applyAsDouble((T) this));

        return (T) this;
    }

    public T size(int width, int height) {
        return this.width(width).height(height);
    }

    public T size(int size) {
        return this.size(size, size);
    }

    public void renderGuiItem(Item item, int x, int y) {
        this.withZ(() -> itemRenderer.renderGuiItemIcon(item.getDefaultStack(), x, y));
    }

    /**
     @return the total width of the smallest area that contains this node's descendants
     */
    public int descendantWidth() {
        return switch (this.degree()) {
            case 0 -> 0;
            case 1 -> this.child(0).width();
            default -> {
                var filter = this.width.type == Length.Type.CHILD_SUM;
                yield (filter ? this.descendants().filter(node -> node.width.type != Length.Type.PARENT_PROPORTION) : this.descendants()).mapToInt(B::absoluteEndX).max().orElse(0)
                    - (filter ? this.descendants().filter(node -> node.width.type != Length.Type.PARENT_PROPORTION) : this.descendants()).mapToInt(B::absoluteX).min().orElse(0);
            }
        };
    }

    /**
     @return the total height of the smallest area that contains this node's descendants
     */
    public int descendantHeight() {
        return switch (this.degree()) {
            case 0 -> 0;
            case 1 -> this.child(0).height();
            default -> {
                var filter = this.height.type == Length.Type.CHILD_SUM;
                yield (filter ? this.descendants().filter(node -> node.height.type != Length.Type.PARENT_PROPORTION) : this.descendants()).mapToInt(B::absoluteEndY).max().orElse(0)
                    - (filter ? this.descendants().filter(node -> node.height.type != Length.Type.PARENT_PROPORTION) : this.descendants()).mapToInt(B::absoluteY).min().orElse(0);
            }
        };
    }

    /**
     @return the number of children that this node has
     */
    public int degree() {
        return this.listChildren().size();
    }

    /**
     @return this node's index in the parent element's node list; -1 if this node is the root
     */
    public int index() {
        return this.parent().map(parent -> parent.listChildren().indexOf((B) this)).orElse(-1);
    }

    /**
     @return whether this node is the root
     */
    public boolean isRoot() {
        return this.parent().isEmpty();
    }

    /**
     Apply a {@link Consumer} with {@code this} to and return {@code this}.

     @param consumer a consumer to apply with {@code this}
     @return {@code this}
     */
    public T with(Consumer<? super T> consumer) {
        consumer.accept((T) this);

        return (T) this;
    }

    /**
     Apply a {@link Function} to {@code this} and return its result.

     @param transform a function to apply to {@code this}
     @param <R> the type of the result
     @return the result
     */
    public <R> R transform(Function<? super T, R> transform) {
        return transform.apply((T) this);
    }

    /**
     @return this node's parent
     */
    public Optional<? extends B> parent() {
        return Optional.empty();
    }

    /**
     @return a list of this node's children
     */
    public List<? extends B> listChildren() {
        return List.of();
    }

    /**
     @return a stream of this node's children
     */
    public Stream<? extends B> children() {
        return this.listChildren().stream();
    }

    /**
     @return a stream of this node's children in reverse order
     */
    public Stream<? extends B> childrenReverse() {
        return Stream.of(this.listChildren().listIterator(this.degree())).mapMulti((iterator, buffer) -> {
            while (iterator.hasPrevious()) {
                buffer.accept(iterator.previous());
            }
        });
    }

    /**
     @return a stream of this node's ancestors
     */
    public Stream<? extends B> ancestors() {
        return this.parent().stream().mapMulti((parent, buffer) -> {
            buffer.accept(parent);
            parent.ancestors().forEach(buffer);
        });
    }

    /**
     @return a stream of this node's posterity
     */
    public Stream<? extends B> descendants() {
        return Stream.concat(this.children(), this.children().flatMap(B::descendants));
    }

    /**
     @return a stream of this node's {@linkplain #isHovered hovered} children
     */
    public Stream<? extends B> hoveredChildren() {
        return this.childrenReverse().filter(B::isHovered);
    }

    /**
     @return the currently hovered descendant rooted at this node
     */
    public Optional<? extends B> hoveredDescendant() {
        return this.childrenReverse()
            .map(B::hovered)
            .filter(Optional::isPresent)
            .findFirst()
            .map(Optional::get);
    }

    /**
     @return the currently hovered node starting at this node as the root
     */
    public Optional<? extends B> hovered() {
        return this.hoveredDescendant().or(() -> Optional.ofNullable(this.isHovered() ? Util.cast(this) : null));
    }

    /**
     @return a stream of this node's {@linkplain #isFocused focused} children
     */
    public Stream<? extends B> focusedChildren() {
        return this.childrenReverse().filter(B::isFocused);
    }

    /**
     @return the currently focused descendant rooted at this node
     */
    public Optional<? extends B> focusedDescendant() {
        return this.childrenReverse()
            .map(B::focused)
            .flatMap(Optional::stream)
            .findFirst();
    }

    /**
     @return the currently focused node starting at this node as the root
     */
    public Optional<? extends B> focused() {
        return this.focusedDescendant().or(() -> Optional.ofNullable(this.isFocused() ? Util.cast(this) : null));
    }

    /**
     Check whether a node is this node's child.

     @param node the node to test
     @return whether {@code node} is this node's child
     */
    public boolean contains(B node) {
        return this.listChildren().contains(node);
    }

    /**
     Get the child node at an index.

     @param index the index of the child node
     @return the child node
     */
    public B child(int index) {
        return this.listChildren().get(index);
    }

    /**
     @return the root node in this node's hierarchy
     */
    public B root() {
        return this.parent().map(B::root).orElse((B) this);
    }

    /**
     @return whether this node should be treated as existent
     */
    public boolean isPresent() {
        return this.parent().isEmpty() || this.parent().get().isPresent();
    }

    /**
     @return whether this node should be rendered
     */
    public boolean isVisible() {
        return this.parent().isEmpty() || this.parent().get().isVisible();
    }

    /**
     @return whether this node is active
     */
    public boolean isActive() {
        return this.parent().isEmpty() || this.parent().get().isActive();
    }

    public boolean isFocused() {
        return false;
    }

    /**
     @return whether this node is hovered my the cursor
     */
    public boolean isHovered() {
        return this.contains(Widget.mouseX(), Widget.mouseY());
    }

    /**
     @return whether this node is the first in its hierarchy that is hovered by the cursor
     */
    public boolean isHoveredFirst() {
        return this.root().hovered().filter(this::equals).isPresent();
    }

    /**
     @return whether this node's area contains the given point
     */
    public boolean contains(double x, double y) {
        return Widget.contains(x, y, this.absoluteX(), this.absoluteY(), this.width(), this.height());
    }

    /**
     Invoked every tick.
     */
    public void tick() {
        if (this.isPresent()) {
            this.listChildren().forEach(B::tick);
        }
    }

    @Override
    public void render(MatrixStack matrixes, int mouseX, int mouseY, float delta) {
        if (this.isPresent()) {
            this.listChildren().forEach(child -> child.render(matrixes, mouseX, mouseY, delta));
        }
    }

    /**
     {@inheritDoc}
     */
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (this.isPresent()) {
            this.listChildren().forEach(child -> child.mouseMoved(mouseX, mouseY));
        }
    }

    /**
     {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isPresent() && this.childrenReverse().anyMatch(child -> child.mouseClicked(mouseX, mouseY, button));
    }

    /**
     {@inheritDoc}
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.isPresent() && this.childrenReverse().anyMatch(child -> child.mouseReleased(mouseX, mouseY, button));
    }

    /**
     {@inheritDoc}
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.isPresent() && this.childrenReverse().anyMatch(child -> child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY));
    }

    /**
     {@inheritDoc}
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.isPresent() && this.childrenReverse().anyMatch(widget -> widget.mouseScrolled(mouseX, mouseY, amount));
    }

    /**
     {@inheritDoc}
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.isPresent() && this.childrenReverse().anyMatch(child -> child.keyPressed(keyCode, scanCode, modifiers));
    }

    /**
     {@inheritDoc}
     */
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.isPresent() && this.childrenReverse().anyMatch(child -> child.keyReleased(keyCode, scanCode, modifiers));
    }

    /**
     {@inheritDoc}
     */
    @Override
    public boolean charTyped(char character, int modifiers) {
        return this.isPresent() && this.childrenReverse().anyMatch(child -> child.charTyped(character, modifiers));
    }

    /**
     {@inheritDoc}
     */
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.isPresent() && this.contains(mouseX, mouseY);
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

    protected int resolve(Length length, int parent) {
        return length.value.getAsInt() + (int) switch (length.type) {
            case EXACT -> length.base().getAsDouble();
            case PARENT_PROPORTION -> length.base().getAsDouble() * parent;
            default -> throw new IllegalArgumentException();
        };
    }
}
