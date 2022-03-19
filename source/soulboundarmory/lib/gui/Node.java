package soulboundarmory.lib.gui;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import soulboundarmory.util.Util;

/**
 A node in a tree of GUI elements.

 @param <B> the base type wherewith this node can interact (have as parents or children)
 @param <T> the type of the node
 */
public interface Node<B extends Node<B, ?>, T extends Node<B, T>> extends Drawable, Element {
    MinecraftClient client = MinecraftClient.getInstance();
    Window window = client.getWindow();
    Keyboard keyboard = client.keyboard;
    Mouse mouse = client.mouse;
    TextRenderer textRenderer = client.textRenderer;
    TextureManager textureManager = client.textureManager;
    TextHandler textHandler = textRenderer.getTextHandler();
    ResourceManager resourceManager = client.getResourceManager();
    ItemRenderer itemRenderer = client.getItemRenderer();
    EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderDispatcher();
    InGameHud hud = client.inGameHud;
    BakedModelManager bakedModelManager = client.getBakedModelManager();
    GameRenderer gameRenderer = client.gameRenderer;
    SoundManager soundManager = client.getSoundManager();

    /**
     @return this node's current x coordinate
     */
    int x();

    /**
     @return this node's current y coordinate
     */
    int y();

    /**
     @return this node's current width
     */
    int width();

    /**
     @return this node's current height
     */
    int height();

    /**
     @return the x coordinate of this node's center
     */
    default int middleX() {
        return this.x() + this.width() / 2;
    }

    /**
     @return the y coordinate of this node's center
     */
    default int middleY() {
        return this.y() + this.height() / 2;
    }

    /**
     @return the x coordinate at the end of this node
     */
    default int endX() {
        return this.x() + this.width();
    }

    /**
     @return the y coordinate at the end of this node
     */
    default int endY() {
        return this.y() + this.height();
    }

    /**
     @return the total width of this node and all of its descendants
     */
    default int totalWidth() {
        return this.descendants().mapToInt(B::x).max().orElseGet(this::endX) - this.descendants().mapToInt(B::x).min().orElseGet(this::x);
    }

    /**
     @return the total height of this node and all of its descendants
     */
    default int totalHeight() {
        return this.descendants().mapToInt(B::y).max().orElseGet(this::endY) - this.descendants().mapToInt(B::y).min().orElseGet(this::y);
    }

    /**
     @return the number of children that this node has
     */
    default int degree() {
        return this.listChildren().size();
    }

    /**
     @return this node's index in the parent element's node list; empty if this node is the root
     */
    default int index() {
        return this.parent().map(parent -> parent.listChildren().indexOf((B) this)).orElse(-1);
    }

    /**
     @return whether this node is the root
     */
    default boolean isRoot() {
        return this.parent().isEmpty();
    }

    /**
     Apply a consumer to and return {@code this}.

     @param consumer a consumer to apply
     @return {@code this}
     */
    default T with(Consumer<? super T> consumer) {
        consumer.accept((T) this);

        return (T) this;
    }

    /**
     Apply a fuction to {@code this} and return its result.

     @param transform a function to apply
     @param <R> the type of the result
     @return the result
     */
    default <R> R transform(Function<? super T, R> transform) {
        return transform.apply((T) this);
    }

    /**
     @return this node's parent
     */
    default Optional<? extends B> parent() {
        return Optional.empty();
    }

    /**
     @return a list of this node's children
     */
    default List<? extends B> listChildren() {
        return List.of();
    }

    /**
     @return a stream of this node's children
     */
    default Stream<? extends B> children() {
        return this.listChildren().stream();
    }

    /**
     @return a stream of this node's children in reverse order
     */
    default Stream<? extends B> childrenReverse() {
        return Stream.of(this.listChildren().listIterator(this.degree())).mapMulti((iterator, buffer) -> {
            while (iterator.hasPrevious()) {
                buffer.accept(iterator.previous());
            }
        });
    }

    /**
     @return a stream of this node's ancestors
     */
    default Stream<? extends B> ancestors() {
        return this.parent().stream().mapMulti((parent, buffer) -> {
            buffer.accept(parent);
            parent.ancestors().forEach(buffer);
        });
    }

    /**
     @return a stream of this node's posterity
     */
    default Stream<? extends B> descendants() {
        return this.children().flatMap(B::descendants);
    }

    /**
     @return a stream of this node's {@linkplain #isHovered hovered} children
     */
    default Stream<? extends B> hoveredChildren() {
        return this.childrenReverse().filter(B::isHovered);
    }

    /**
     @return the currently hovered descendant rooted at this node
     */
    default Optional<? extends B> hoveredDescendant() {
        return this.childrenReverse()
            .map(B::hovered)
            .filter(Optional::isPresent)
            .findFirst()
            .map(Optional::get);
    }

    /**
     @return the currently hovered node starting at this node as the root
     */
    default Optional<? extends B> hovered() {
        return this.hoveredDescendant().or(() -> Optional.ofNullable(this.isHovered() ? Util.cast(this) : null));
    }

    /**
     Check whether a node is this node's child.

     @param node the node to test
     @return whether {@code node} is this node's child
     */
    default boolean contains(B node) {
        return this.listChildren().contains(node);
    }

    /**
     Get the child node at an index.

     @param index the index of the child node
     @return the child node
     */
    default B child(int index) {
        return this.listChildren().get(index);
    }

    /**
     @return the root node in this node's hierarchy
     */
    default B root() {
        return this.parent().map(B::root).orElse((B) this);
    }

    /**
     @return whether this node should be treated as existent
     */
    default boolean isPresent() {
        return this.parent().isEmpty() || this.parent().get().isPresent();
    }

    /**
     @return whether this node should be rendered
     */
    default boolean isVisible() {
        return this.parent().isEmpty() || this.parent().get().isVisible();
    }

    /**
     @return whether this node is active
     */
    default boolean isActive() {
        return this.parent().isEmpty() || this.parent().get().isActive();
    }

    /**
     @return whether this node is hovered my the cursor
     */
    default boolean isHovered() {
        return this.contains(CellElement.mouseX(), CellElement.mouseY());
    }

    /**
     @return whether this node is the first in its hierarchy that is hovered by the cursor
     */
    default boolean isHoveredFirst() {
        return this.root().hovered().filter(this::equals).isPresent();
    }

    /**
     @return whether this node's area contains the given point
     */
    default boolean contains(double x, double y) {
        return CellElement.contains(x, y, this.x(), this.y(), this.width(), this.height());
    }

    /**
     Invoked every tick.
     */
    default void tick() {
        if (this.isPresent()) {
            this.listChildren().forEach(B::tick);
        }
    }

    @Override
    default void render(MatrixStack matrixes, int mouseX, int mouseY, float delta) {
        if (this.isPresent()) {
            this.listChildren().forEach(child -> child.render(matrixes, mouseX, mouseY, delta));
        }
    }

    /**
     {@inheritDoc}
     */
    @Override
    default void mouseMoved(double mouseX, double mouseY) {
        if (this.isPresent()) {
            this.listChildren().forEach(child -> child.mouseMoved(mouseX, mouseY));
        }
    }

    /**
     {@inheritDoc}
     */
    @Override
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isPresent() && this.childrenReverse().anyMatch(child -> child.mouseClicked(mouseX, mouseY, button));
    }

    /**
     {@inheritDoc}
     */
    @Override
    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.isPresent() && this.childrenReverse().anyMatch(child -> child.mouseReleased(mouseX, mouseY, button));
    }

    /**
     {@inheritDoc}
     */
    @Override
    default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.isPresent() && this.childrenReverse().anyMatch(child -> child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY));
    }

    /**
     {@inheritDoc}
     */
    @Override
    default boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.isPresent() && this.childrenReverse().anyMatch(widget -> widget.mouseScrolled(mouseX, mouseY, amount));
    }

    /**
     {@inheritDoc}
     */
    @Override
    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.isPresent() && this.childrenReverse().anyMatch(child -> child.keyPressed(keyCode, scanCode, modifiers));
    }

    /**
     {@inheritDoc}
     */
    @Override
    default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.isPresent() && this.childrenReverse().anyMatch(child -> child.keyReleased(keyCode, scanCode, modifiers));
    }

    /**
     {@inheritDoc}
     */
    @Override
    default boolean charTyped(char character, int modifiers) {
        return this.isPresent() && this.childrenReverse().anyMatch(child -> child.charTyped(character, modifiers));
    }

    /**
     {@inheritDoc}
     */
    @Override
    default boolean isMouseOver(double mouseX, double mouseY) {
        return this.isPresent() && this.contains(mouseX, mouseY);
    }
}
