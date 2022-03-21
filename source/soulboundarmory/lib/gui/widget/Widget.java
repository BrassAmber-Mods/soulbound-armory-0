package soulboundarmory.lib.gui.widget;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46C;
import soulboundarmory.function.NulliPredicate;
import soulboundarmory.lib.gui.AbstractNode;
import soulboundarmory.lib.gui.Node;
import soulboundarmory.lib.gui.coordinate.Coordinate;
import soulboundarmory.lib.gui.coordinate.Offset;
import soulboundarmory.lib.gui.widget.callback.PressCallback;
import soulboundarmory.lib.gui.widget.scroll.ContextScrollAction;
import soulboundarmory.lib.gui.widget.scroll.ScrollAction;
import soulboundarmory.util.Util;

/**
 A flexible and fluent element that supports nesting.

 @param <T> the type of the widget
 */
public class Widget<T extends Widget<T>> extends AbstractNode<Widget<?>, T> implements TooltipComponent {
    public Optional<Widget<?>> parent = Optional.empty();
    public ReferenceArrayList<Widget<?>> children = ReferenceArrayList.of();
    public ReferenceArrayList<Widget<?>> tooltips = ReferenceArrayList.of();

    /**
     The element selected by the keyboard; may be `null`, `this` or a child.
     */
    public Optional<Widget<?>> selected = Optional.empty();
    public PressCallback<T> primaryAction;
    public PressCallback<T> secondaryAction;
    public PressCallback<T> tertiaryAction;
    public ContextScrollAction<T> scrollAction;

    public NulliPredicate present = () -> !this.isTooltip() || this.isPresentTooltip();
    public NulliPredicate visible = NulliPredicate.ofTrue();
    public NulliPredicate active = NulliPredicate.ofTrue();

    public IntSupplier x = () -> super.x.resolve(this.width(), this.parent.map(Widget::x).orElse(0), this.parent.map(Widget::width).orElse(windowWidth()));
    public IntSupplier y = () -> super.y.resolve(this.height(), this.parent.map(Widget::y).orElse(0), this.parent.map(Widget::height).orElse(windowHeight()));

    /**
     Is the deepest element that is hovered by the mouse.
     */
    public boolean mouseFocused;
    public boolean dragging;

    /**
     Stored by {@link #render(MatrixStack)} in order to avoid passing it around everywhere.
     */
    public MatrixStack matrixes;

    private final Set<Widget<?>> renderDeferred = ReferenceLinkedOpenHashSet.of();

    public void initialize() {}

    public void drag() {}

    public void drop() {}

    public T x(Offset.Type offset) {
        super.x.offset.type = offset;

        return (T) this;
    }

    public T x(Coordinate.Position position) {
        super.x.position = position;

        return (T) this;
    }

    /**
     Position this widget horizontally at a point—expressed as a fraction of its width—along the parent element offset from its origin.

     @param value the fraction of the parent's width
     @return this
     */
    public T x(double value) {
        super.x.offset.value = value;

        return this.x(Offset.Type.RELATIVE);
    }

    public T x(double offset, int x) {
        return this.x(offset).x(x).x(Offset.Type.RELATIVE);
    }

    public T x(IntSupplier x) {
        this.x = x;

        return (T) this;
    }

    public T x(Node<?, ?> node) {
        return this.x(node::x);
    }

    public T y(Offset.Type offset) {
        super.y.offset.type = offset;

        return (T) this;
    }

    public T y(Coordinate.Position position) {
        super.y.position = position;

        return (T) this;
    }

    /**
     Position this widget vertically offset from the origin of the parent element at a point expressed as a fraction of the parent element's height.

     @param y y position along the parent element as a fraction of its height
     @return this
     */
    public T y(double y) {
        super.y.offset.value = y;

        return this.y(Offset.Type.RELATIVE);
    }

    public T y(double offset, int y) {
        return this.y(offset).y(y).y(Offset.Type.RELATIVE);
    }

    public T y(IntSupplier y) {
        this.y = y;

        return (T) this;
    }

    public T y(Node<?, ?> node) {
        return this.y(node::y);
    }

    public T offset(Offset.Type offset) {
        return this.x(offset).y(offset);
    }

    public T position(Coordinate.Position position) {
        return this.x(position).y(position);
    }

    public T centerX() {
        return this.x(Coordinate.Position.CENTER);
    }

    public T centerY() {
        return this.y(Coordinate.Position.CENTER);
    }

    public T center() {
        return this.position(Coordinate.Position.CENTER);
    }

    public T present(NulliPredicate predicate) {
        this.present = predicate;

        return (T) this;
    }

    public T present(boolean present) {
        return this.present(NulliPredicate.of(present));
    }

    public T visible(NulliPredicate predicate) {
        this.visible = predicate;

        return (T) this;
    }

    public T active(NulliPredicate active) {
        this.active = active;

        return (T) this;
    }

    public T active(boolean active) {
        return this.active(NulliPredicate.of(active));
    }

    public T text(String text) {
        return this.text(new TranslatableText(text));
    }

    public T text(Text text) {
        this.text(widget -> widget.text(text));

        return (T) this;
    }

    public T text(Consumer<TextWidget> configure) {
        return this.with(self -> {
            var text = new TextWidget().center().x(.5).y(.5);
            configure.accept(text);
            self.add(text);
        });
    }

    public T parent(Widget<?> parent) {
        this.parent = Optional.ofNullable(parent);

        return (T) this;
    }

    /**
     @see #isValidPrimaryClick
     @see #isValidPrimaryKey
     */
    public T primaryAction(PressCallback<T> action) {
        this.primaryAction = action;

        return (T) this;
    }

    public T secondaryAction(PressCallback<T> action) {
        this.secondaryAction = action;

        return (T) this;
    }

    public T tertiaryAction(PressCallback<T> action) {
        this.tertiaryAction = action;

        return (T) this;
    }

    public T primaryAction(Runnable action) {
        return this.primaryAction(widget -> action.run());
    }

    public T secondaryAction(Runnable action) {
        return this.secondaryAction(widget -> action.run());
    }

    public T tertiaryAction(Runnable action) {
        return this.tertiaryAction(widget -> action.run());
    }

    public T scrollAction(ContextScrollAction<T> action) {
        this.scrollAction = action;

        return (T) this;
    }

    public T scrollAction(ScrollAction<T> action) {
        return this.scrollAction((ContextScrollAction<T>) action);
    }

    public T tooltip(Widget<?> tooltip) {
        this.tooltips.add(this.add(tooltip));

        return (T) this;
    }

    public T select(Widget<?> widget) {
        if (widget == this) {
            this.selected = Optional.of(this);
            this.select();
        } else if (widget == null || this.contains(widget)) {
            this.selected = Optional.ofNullable(widget);
        } else {
            throw new NoSuchElementException();
        }

        return (T) this;
    }

    public <C extends Widget> C add(int index, C child) {
        child.parent(this);
        this.children.add(index, child);

        return child;
    }

    public <C extends Widget> C add(C child) {
        return this.add(this.degree(), child);
    }

    public T add(int index, Iterable<? extends Widget<?>> children) {
        for (var child : children) {
            this.add(index++, child);
        }

        return (T) this;
    }

    public T add(Iterable<? extends Widget<?>> children) {
        return this.add(this.degree(), children);
    }

    public boolean add(int index, Widget<?>... children) {
        var added = false;

        for (var child : children) {
            this.add(index++, child);
            added = true;
        }

        return added;
    }

    public boolean add(Widget<?>... children) {
        return this.add(this.degree(), children);
    }

    public T with(Widget<?> child) {
        this.add(child);

        return (T) this;
    }

    public <C extends Widget> C remove(C child) {
        this.children.remove(child);
        this.tooltips.remove(child);
        child.parent(null);

        return child;
    }

    public boolean remove(Iterable<? extends Widget<?>> children) {
        var removed = false;

        for (var child : children) {
            this.remove(child);
            removed = true;
        }

        return removed;
    }

    public boolean remove(Widget<?>... children) {
        return this.remove(List.of(children));
    }

    public void clear() {
        this.children.clear();
        this.tooltips.clear();
    }

    public int replace(Widget<?> original, Widget<?> replacement) {
        var index = original == null ? -1 : original.index();

        if (index >= 0) {
            this.children.set(index, replacement);
            original.parent(null);
            replacement.parent(this);
        }

        return index;
    }

    public int renew(Widget<?> original, Widget<?> replacement) {
        var index = this.replace(original, replacement);

        if (index < 0) {
            this.add(replacement);
        }

        return index;
    }

    @Override
    public int x() {
        return this.x.getAsInt();
    }

    @Override
    public int y() {
        return this.y.getAsInt();
    }

    @Override
    public int z() {
        return super.z() + this.parent.map(Widget::z).orElse(0);
    }

    @Override
    public boolean isPresent() {
        return this.present.getAsBoolean() && super.isPresent();
    }

    @Override
    public boolean isVisible() {
        return this.visible.getAsBoolean() && super.isVisible();
    }

    @Override
    public boolean isActive() {
        return this.active.getAsBoolean() && super.isActive();
    }

    public boolean isSelected() {
        return this.selected.filter(this::equals).isPresent();
    }

    @Override public boolean isFocused() {
        return this.mouseFocused || this.isSelected();
    }

    public boolean focusable() {
        return this.isActive() && (this.primaryAction != null || this.secondaryAction != null || this.tertiaryAction != null || !this.tooltips.isEmpty());
    }

    public boolean scrollable() {
        return this.scrollAction != null;
    }

    public boolean isTooltip() {
        return this.parent.filter(parent -> parent.tooltips.contains(this)).isPresent();
    }

    public boolean isPresentTooltip() {
        return this.isTooltip() && this.parent.get().mouseFocused || this.parent.get().isSelected() && isControlDown() || this.isFocused();
    }

    @Override
    public Optional<? extends Widget<?>> parent() {
        return this.parent;
    }

    @Override
    public List<? extends Widget<?>> listChildren() {
        return this.children;
    }

    public void preinitialize() {
        this.select(null);
        this.clear();
        keyboard.setRepeatEvents(true);
        this.initialize();
    }

    public Optional<Widget<?>> selectedChild() {
        return this.selected.filter(widget -> widget != this);
    }

    /**
     Select the previous or next {@link #focusable} element.
     <br><br>
     If none of this element and its children is selected, then try to select <br>
     - if {@code forward}, this element; <br>
     - otherwise, the last child of this element. <br>

     @return {@code true} if an element has been selected
     */
    @Override
    public boolean changeFocus(boolean forward) {
        if (this.isPresent()) {
            var direction = forward ? 1 : -1;
            var degree = this.degree();
            int start;

            if (this.isSelected()) {
                if (forward) {
                    start = 0;
                } else if (this.isRoot()) {
                    start = degree - 1;
                } else {
                    this.select(null);

                    return false;
                }
            } else if (this.selected.isPresent()) {
                start = this.selected.get().index();
            } else if (forward && this.focusable()) {
                this.select(this);

                return true;
            } else {
                start = forward ? 0 : degree - 1;
            }

            for (var index = start; (forward || index >= 0) && index < degree; index += direction) {
                if (this.child(index).changeFocus(forward)) {
                    this.select(this.child(index));

                    return true;
                }
            }

            if (!forward && this.focusable()) {
                this.select(this);

                return true;
            }

            if (this.isRoot()) {
                if (this.focusable()) {
                    this.select(this);

                    return true;
                }

                for (var index = forward ? 0 : degree - 1; forward ? index < start : index > start && index < degree; index += direction) {
                    if (this.child(index).changeFocus(forward)) {
                        this.select(this.child(index));

                        return true;
                    }
                }
            }
        }

        this.select(null);

        return false;
    }

    public void select() {}

    public void render(MatrixStack matrixes) {
        if (this.isPresent()) {
            this.renderDeferred.clear();
            this.matrixes = matrixes;
            this.mouseFocused = false;

            if (this.isHovered()) {
                mouseFocused: if (this.focusable()) {
                    for (var ancestor : Util.iterate(this.ancestors())) {
                        if (ancestor.z() > this.z() && ancestor.mouseFocused) {
                            break mouseFocused;
                        }

                        ancestor.mouseFocused = false;
                    }

                    this.mouseFocused = true;
                }
            }

            if (this.isVisible()) {
                this.render();

                for (var child : this.children) {
                    child.render(matrixes);
                }

                if (this.isRoot()) {
                    this.renderDeferred.forEach(widget -> {
                        RenderSystem.clear(GL46C.GL_DEPTH_BUFFER_BIT, false);
                        widget.render(matrixes);
                    });
                }
            }
        }
    }

    @Override
    public void render(MatrixStack matrixes, int mouseX, int mouseY, float delta) {
        this.render(matrixes);
    }

    protected void render() {}

    protected void deferRender() {
        this.root().renderDeferred.add(this);
    }

    /**
     Determine whether the click should trigger the primary action.

     @return `true` for left click (0) by default
     */
    public boolean isValidPrimaryClick(int button) {
        return this.primaryAction != null && button == 0;
    }

    /**
     Determine whether the click should trigger the secondary action.

     @return `true` for right click (1) by default
     */
    public boolean isValidSecondaryClick(int button) {
        return this.secondaryAction != null && button == 1;
    }

    /**
     Determine whether the click should trigger the tertiary action.

     @return `true` for middle click (2) by default
     */
    public boolean isValidTertiaryClick(int button) {
        return this.tertiaryAction != null && button == 2;
    }

    /**
     Determine whether the key press should trigger the primary action.

     @return `true` for the space bar by default
     */
    public boolean isValidPrimaryKey(int keyCode, int scanCode, int modifiers) {
        return this.primaryAction != null && this.isValidActionKey(keyCode, scanCode, modifiers);
    }

    /**
     Determine whether the key press should trigger the secondary action.

     @return `true` for the space bar when a shift key is pressed by default
     */
    public boolean isValidSecondaryKey(int keyCode, int scanCode, int modifiers) {
        return this.secondaryAction != null && this.isValidActionKey(keyCode, scanCode, modifiers) && (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
    }

    /**
     Determine whether the key press should trigger the tertiary action.

     @return `true` for the space bar when a control key is pressed by default
     */
    public boolean isValidTertiaryKey(int keyCode, int scanCode, int modifiers) {
        return this.tertiaryAction != null && this.isValidActionKey(keyCode, scanCode, modifiers) && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
    }

    /**
     Determine whether the key press is a valid action key.

     @return `true` for space bar and return and enter keys
     */
    public boolean isValidActionKey(int keyCode, int scanCode, int modifiers) {
        return keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER;
    }

    /**
     Push a matrix, translate by {@link #z()}, run {@code render} and pop.
     */
    @Override public void withZ(Runnable render) {
        this.matrixes.push();
        this.matrixes.translate(0, 0, this.z());
        super.withZ(render);
        this.matrixes.pop();
    }

    public void renderBackground() {
        super.renderBackground(this.matrixes);
    }

    public void renderTooltip(List<? extends StringVisitable> lines) {
        renderTooltip(this.matrixes, lines, mouseX(), mouseY());
    }

    public void renderTooltip(StringVisitable text) {
        renderTooltip(this.matrixes, text, mouseX(), mouseY());
    }

    public void renderTooltip(double x, double y, List<? extends StringVisitable> lines) {
        renderTooltip(this.matrixes, lines, x, y);
    }

    public void renderTooltip(double x, double y, StringVisitable text) {
        renderTooltip(this.matrixes, text, x, y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isPresent()) {
            if (super.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }

            if (this.clicked()) {
                if (this.isValidPrimaryClick(button)) {
                    this.primaryPress();
                    this.primaryClick();
                } else if (this.isValidSecondaryClick(button)) {
                    this.secondaryPress();
                    this.secondaryClick();
                } else if (this.isValidTertiaryClick(button)) {
                    this.tertiaryPress();
                    this.tertiaryClick();
                } else {
                    return false;
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isPresent()) {
            if (super.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }

            if (this.dragging) {
                this.dragging = false;
                this.drop();

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isPresent()) {
            if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }

            if (this.dragging) {
                this.drag();

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.isPresent()) {
            if (super.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }

            if (this.scrollable() && this.mouseFocused) {
                this.scroll(amount);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isPresent()) {
            if (keyCode == GLFW.GLFW_KEY_TAB && this.changeFocus((modifiers & GLFW.GLFW_MOD_SHIFT) == 0) || super.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }

            if (this.isSelected()) {
                if (this.isValidPrimaryKey(keyCode, scanCode, modifiers)) {
                    this.primaryPress();
                } else if (this.isValidSecondaryKey(keyCode, scanCode, modifiers)) {
                    this.secondaryPress();
                } else if (this.isValidTertiaryKey(keyCode, scanCode, modifiers)) {
                    this.tertiaryPress();
                } else {
                    return false;
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrixes, ItemRenderer itemRenderer, int z) {
        this.x(x).y(y).z(z).render(matrixes);
    }

    @Override
    public int getHeight() {
        return this.height();
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.width();
    }

    protected boolean clicked() {
        return this.isActive() && this.mouseFocused;
    }

    protected void playSound() {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
    }

    protected void press() {
        this.playSound();
    }

    protected void primaryPress() {
        this.press();
        this.execute(this.primaryAction);
    }

    protected void secondaryPress() {
        this.press();
        this.execute(this.secondaryAction);
    }

    protected void tertiaryPress() {
        this.press();
        this.execute(this.tertiaryAction);
    }

    protected void primaryClick() {
        if (this.focusable()) {
            this.dragging = true;
        }
    }

    protected void secondaryClick() {}

    protected void tertiaryClick() {}

    protected void scroll(double amount) {
        if (this.scrollAction != null) {
            this.scrollAction.scroll((T) this, amount);
        }
    }

    protected void execute(PressCallback<T> callback) {
        if (callback != null) {
            callback.onPress((T) this);
        }
    }
}
