package cell.client.gui.widget;

import cell.client.gui.CellElement;
import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.callback.TextProvider;
import cell.client.gui.widget.callback.TooltipProvider;
import cell.client.gui.widget.callback.TooltipRenderer;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

/**
 A flexible element that supports nesting.

 @param <T> the type of the widget
 */
public abstract class Widget<T extends Widget<T>> extends CellElement<T> {
    protected static final SoundManager soundManager = minecraft.getSoundManager();

    public ReferenceArrayList<Widget<?>> children = new ReferenceArrayList<>();
    public Optional<Widget<?>> parent = Optional.empty();
    public Optional<Widget<?>> tooltipWidget = Optional.empty();

    /**
     The element selected by the keyboard; may be `null`, `this` or a child.
     */
    public Optional<Widget<?>> selected = Optional.empty();
    public PressCallback<T> primaryAction;
    public PressCallback<T> secondaryAction;
    public PressCallback<T> tertiaryAction;
    public TooltipRenderer<T> tooltip;

    public boolean centerX;
    public boolean centerY;
    public boolean active = true;
    public boolean visible = true;

    /**
     Is the deepest element that is hovered by the mouse.
     */
    public boolean mouseFocused;
    public boolean dragging;

    /**
     Stored in {@link #render(MatrixStack, int, int, float)} in order to avoid passing it around everywhere.
     */
    protected MatrixStack matrixes;

    public void initialize() {}

    public void drag() {}

    public void drop() {}

    public T x(Coordinate.Type type) {
        this.x.type = type;

        return (T) this;
    }

    public T y(Coordinate.Type type) {
        this.y.type = type;

        return (T) this;
    }

    public T position(Coordinate.Type type) {
        return this.x(type).y(type);
    }

    public T centerX(boolean center) {
        this.centerX = center;

        return (T) this;
    }

    public T centerX() {
        return this.centerX(true);
    }

    public T centerY(boolean center) {
        this.centerY = center;

        return (T) this;
    }

    public T centerY() {
        return this.centerY(true);
    }

    public T center(boolean center) {
        return this.centerX(center).centerY(center);
    }

    public T center() {
        return this.center(true);
    }

    public T active(boolean active) {
        this.active = active;

        return (T) this;
    }

    public T text(String text) {
        return this.text(new TranslatableText(text));
    }

    public T text(Text text) {
        this.add(new TextWidget().text(text).position(Coordinate.Type.CENTER).width(textDrawer.getWidth(text)).height(fontHeight()).center());

        return (T) this;
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

    public T tooltip(String tooltip) {
        return this.tooltip((T widget, double mouseX, double mouseY) -> new TranslatableText(tooltip));
    }

    public T tooltip(Text... tooltip) {
        return this.tooltip(Arrays.asList(tooltip));
    }

    public T tooltip(List<Text> tooltip) {
        return this.tooltip((T widget, double mouseX, double mouseY) -> tooltip);
    }

    public T tooltip(TooltipProvider<T> tooltipProvider) {
        return this.tooltip((TooltipRenderer<T>) tooltipProvider);
    }

    public T tooltip(TextProvider<T> textProvider) {
        return this.tooltip((TooltipRenderer<T>) textProvider);
    }

    public T tooltip(TooltipRenderer<T> renderer) {
        this.tooltip = renderer;

        return (T) this;
    }

    public T tooltip(Widget<?> tooltip) {
        this.tooltipWidget = Optional.of(tooltip);

        return (T) this;
    }

    public T select(Widget<?> widget) {
        if (widget == this) {
            this.selected = Optional.of(this);
            this.onSelection();
        } else if (widget == null || this.children.contains(widget)) {
            this.selected = Optional.ofNullable(widget);
        } else {
            throw new NoSuchElementException();
        }

        return (T) this;
    }

    public <C extends Widget> C add(int index, C child) {
        this.children.add(index, child);
        child.parent(this);

        return child;
    }

    public <C extends Widget> C add(C child) {
        return this.add(this.children.size(), child);
    }

    public T add(int index, Iterable<Widget<?>> children) {
        for (var child : children) {
            this.add(index++, child);
        }

        return (T) this;
    }

    public T add(Iterable<Widget<?>> children) {
        return this.add(this.children.size(), children);
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
        return this.add(this.children.size(), children);
    }

    public <C extends Widget> C remove(C child) {
        this.children.remove(child);
        child.parent(null);

        return child;
    }

    public boolean remove(Iterable<Widget<?>> children) {
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

    public int replace(Widget<?> original, Widget<?> replacement) {
        var index = original.index();

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
        var x = this.x.get(this.parent.map(Widget::x).orElse(0), this.parent.map(Widget::width).orElse(0));
        return this.centerX ? x - this.width() / 2 : x;
    }

    @Override
    public int y() {
        var y = this.y.get(this.parent.map(Widget::y).orElse(0), this.parent.map(Widget::height).orElse(0));
        return this.centerY ? y - this.height() / 2 : y;
    }

    public int middleX() {
        return this.x() + this.width() / 2;
    }

    public int middleY() {
        return this.y() + this.height() / 2;
    }

    public int endX() {
        return this.x() + this.width();
    }

    public int endY() {
        return this.y() + this.height();
    }

    public boolean active() {
        return this.active && (this.parent.isEmpty() || this.parent.get().active());
    }

    public boolean hovered() {
        return this.contains(mouseX(), mouseY());
    }

    public boolean selected() {
        return this.selected.isPresent() && this.selected.get() == this;
    }

    public boolean focused() {
        return this.mouseFocused || this.selected();
    }

    public boolean focusable() {
        return this.active() && (this.primaryAction != null || this.secondaryAction != null || this.tertiaryAction != null || this.tooltip != null || this.tooltipWidget.isPresent());
    }

    public boolean isRoot() {
        return this.parent.isEmpty();
    }

    public int index() {
        return this.parent.map(parent -> parent.children.indexOf(this)).orElse(-1);
    }

    public Optional<Widget<?>> root() {
        return this.parent.map(Widget::root).orElseGet(() -> Optional.of(this));
    }

    public Stream<Widget<?>> ancestors() {
        return this.parent.isEmpty() ? Stream.empty() : Stream.iterate(this.parent.get(), parent -> parent.parent.isPresent(), parent -> parent.parent.get());
    }

    public Stream<Widget<?>> children() {
        return this.children.stream();
    }

    public Stream<Widget<?>> childrenReverse() {
        var iterator = this.children.listIterator(this.children.size());
        return iterator.hasPrevious() ? Stream.iterate(iterator.previous(), Objects::nonNull, child -> iterator.hasPrevious() ? iterator.previous() : null) : Stream.empty();
    }

    public Stream<Widget<?>> hoveredChildren() {
        return this.childrenReverse().filter(Widget::hovered);
    }

    public Widget<?> child(int index) {
        return this.children.get(index);
    }

    public void preinitialize() {
        this.select(null);
        this.children.clear();
        this.initialize();
    }

    public Optional<Widget<?>> hoveredWidget() {
        return this.childrenReverse().map(Widget::hoveredWidget).filter(Optional::isPresent).findFirst().orElseGet(() -> Optional.ofNullable(this.contains(mouseX(), mouseY()) ? this : null));
    }

    public Optional<Widget<?>> hoveredChild() {
        return this.hoveredWidget().filter(element -> element != this);
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
     */
    @Override
    public boolean changeFocus(boolean forward) {
        var direction = forward ? 1 : -1;
        var size = this.children.size();
        int start;

        if (this.selected()) {
            if (forward) {
                start = 0;
            } else if (this.isRoot()) {
                start = size - 1;
            } else {
                this.select(null);

                return false;
            }
        } else if (this.selected.isPresent()) {
            start = this.selected.get().index();
        } else if (this.focusable()) {
            this.select(this);

            return true;
        } else {
            start = forward ? 0 : size - 1;
        }

        for (var index = start; (forward || index >= 0) && index < size; index += direction) {
            if (this.child(index).changeFocus(forward)) {
                this.select(this.child(index));

                return true;
            }
        }

        if (this.isRoot()) {
            if (this.focusable()) {
                this.select(this);

                return true;
            }

            for (var index = forward ? 0 : size - 1; forward ? index < start : index > start && index < size; index += direction) {
                if (this.child(index).changeFocus(forward)) {
                    this.select(this.child(index));

                    return true;
                }
            }
        }

        this.select(null);

        return false;
    }

    public void onSelection() {}

    @Override
    public void render(MatrixStack matrixes, int mouseX, int mouseY, float delta) {
        this.matrixes = matrixes;

        this.mouseFocused = false;
        this.tooltipWidget.ifPresent(this.children::remove);

        if (this.visible) {
            if (this.hovered()) {
                if (this.focusable()) {
                    this.mouseFocused = true;
                    this.ancestors().forEach(parent -> parent.mouseFocused = false);
                }

                this.whileHovered();
            }

            if (this.focused()) {
                this.whileFocused();
            }

            this.renderWidget();

            this.children.forEach(child -> child.render(matrixes, mouseX, mouseY, delta));
        }
    }

    public void render(MatrixStack matrixes, float delta) {
        this.render(matrixes, -1, -1, delta);
    }

    public void render(MatrixStack matrixes) {
        this.render(matrixes, -1, -1, 0);
    }

    protected void renderWidget() {}

    protected void renderTooltip() {
        if (this.mouseFocused || Screen.hasShiftDown()) {
            var x = this.mouseFocused ? (int) mouseX() : this.x();
            var y = this.mouseFocused ? (int) mouseY() : this.y();

            if (this.tooltip != null) {
                this.tooltip.render((T) this, this.matrixes, x, y);
            }

            this.tooltipWidget.ifPresent(tooltip -> this.children.add(tooltip.x(x).y(y)));
        }
    }

    protected void whileHovered() {}

    protected void whileFocused() {
        this.renderTooltip();
    }

    /**
     Determine whether the click should trigger the primary action.

     @return `true` for left click (0) by default.
     */
    public boolean isValidPrimaryClick(int button) {
        return this.primaryAction != null && button == 0;
    }

    /**
     Determine whether the click should trigger the secondary action.

     @return `true` for right click (1) by default.
     */
    public boolean isValidSecondaryClick(int button) {
        return this.secondaryAction != null && button == 1;
    }

    /**
     Determine whether the click should trigger the tertiary action.

     @return `true` for middle click (2) by default.
     */
    public boolean isValidTertiaryClick(int button) {
        return this.tertiaryAction != null && button == 2;
    }

    /**
     Determine whether the key press should trigger the primary action.

     @return `true` for the space bar by default.
     */
    public boolean isValidPrimaryKey(int keyCode, int scanCode, int modifiers) {
        return this.primaryAction != null && this.isValidActionKey(keyCode, scanCode, modifiers);
    }

    /**
     Determine whether the key press should trigger the secondary action.

     @return `true` for the space bar when a shift key is pressed by default.
     */
    public boolean isValidSecondaryKey(int keyCode, int scanCode, int modifiers) {
        return this.secondaryAction != null && this.isValidActionKey(keyCode, scanCode, modifiers) && (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
    }

    /**
     Determine whether the key press should trigger the tertiary action.

     @return `true` for the space bar when a control key is pressed by default.
     */
    public boolean isValidTertiaryKey(int keyCode, int scanCode, int modifiers) {
        return this.tertiaryAction != null && this.isValidActionKey(keyCode, scanCode, modifiers) && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
    }

    /**
     Determine whether the key press is a valid action key.

     @return `true` for space bar and return and enter keys.
     */
    public boolean isValidActionKey(int keyCode, int scanCode, int modifiers) {
        return keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER;
    }

    public void renderTooltip(List<? extends StringVisitable> lines, int maxTextWidth) {
        renderTooltip(this.matrixes, lines, mouseX(), mouseY(), maxTextWidth);
    }

    public void renderTooltip(List<? extends StringVisitable> lines) {
        renderTooltip(this.matrixes, lines, mouseX(), mouseY(), -1);
    }

    public void renderTooltip(StringVisitable text, int maxTextWidth) {
        renderTooltip(this.matrixes, text, mouseX(), mouseY(), maxTextWidth);
    }

    public void renderTooltip(StringVisitable text) {
        renderTooltip(this.matrixes, text, mouseX(), mouseY(), -1);
    }

    public void renderTooltip(double x, double y, List<? extends StringVisitable> lines, int maxTextWidth) {
        renderTooltip(this.matrixes, lines, x, y, maxTextWidth);
    }

    public void renderTooltip(double x, double y, List<? extends StringVisitable> lines) {
        renderTooltip(this.matrixes, lines, x, y, -1);
    }

    public void renderTooltip(double x, double y, StringVisitable text, int maxTextWidth) {
        renderTooltip(this.matrixes, text, x, y, maxTextWidth);
    }

    public void renderTooltip(double x, double y, StringVisitable text) {
        renderTooltip(this.matrixes, text, x, y, -1);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.children.forEach(child -> child.mouseMoved(mouseX, mouseY));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.childrenReverse().anyMatch(child -> child.mouseClicked(mouseX, mouseY, button))) {
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

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.childrenReverse().anyMatch(child -> child.mouseReleased(mouseX, mouseY, button))) {
            return true;
        }

        if (this.dragging) {
            this.dragging = false;
            this.drop();

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.childrenReverse().anyMatch(child -> child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))) {
            return true;
        }

        if (this.dragging) {
            this.drag();

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.childrenReverse().anyMatch(widget -> widget.mouseScrolled(mouseX, mouseY, amount));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            if (this.changeFocus((modifiers & GLFW.GLFW_MOD_SHIFT) == 0)) {
                return true;
            }
        }

        if (this.childrenReverse().anyMatch(child -> child.keyPressed(keyCode, scanCode, modifiers))) {
            return true;
        }

        if (this.selected()) {
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

        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.childrenReverse().anyMatch(child -> child.keyReleased(keyCode, scanCode, modifiers));
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        return this.childrenReverse().anyMatch(child -> child.charTyped(character, modifiers));
    }

    @Override
    public void tick() {
        this.children.forEach(Widget::tick);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.contains(mouseX, mouseY);
    }

    protected boolean clicked() {
        return this.active() && this.mouseFocused;
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

    protected void execute(PressCallback<T> callback) {
        if (callback != null) {
            callback.onPress((T) this);
        }
    }
}
