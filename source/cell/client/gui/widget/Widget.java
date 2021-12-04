package cell.client.gui.widget;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.Arrays;
import java.util.List;
import cell.client.gui.CellElement;
import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.callback.TextProvider;
import cell.client.gui.widget.callback.TooltipProvider;
import cell.client.gui.widget.callback.TooltipRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public abstract class Widget<T extends Widget<T>> extends CellElement {
    protected static final SoundManager soundManager = minecraft.getSoundManager();

    public List<CellElement> children = new ReferenceArrayList<>();
    public Text text = LiteralText.EMPTY;
    public PressCallback<T> primaryAction;
    public PressCallback<T> secondaryAction;
    public PressCallback<T> tertiaryAction;
    public TooltipRenderer<T> tooltip;

    public boolean centerX, centerY;

    public boolean active = true;
    public boolean visible = true;
    public boolean hovered;
    public boolean focused;
    public boolean selected;
    public boolean wasHovered;

    protected abstract void renderBackground(MatrixStack matrices, int mouseX, int mouseY, float delta);

    public T x(int x) {
        this.x = x;

        return (T) this;
    }

    public T y(int y) {
        this.y = y;

        return (T) this;
    }

    public T width(int width) {
        this.width.set(width);

        return (T) this;
    }

    public T height(int height) {
        this.height.set(height);

        return (T) this;
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

    public T text(String text) {
        return this.text(new TranslatableText(text));
    }

    public T text(Text text) {
        this.text = text;

        return (T) this;
    }

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
        return this.tooltip((T widget, int mouseX, int mouseY) -> new TranslatableText(tooltip));
    }

    public T tooltip(Text... tooltip) {
        return this.tooltip(Arrays.asList(tooltip));
    }

    public T tooltip(List<Text> tooltip) {
        return this.tooltip((T widget, int mouseX, int mouseY) -> tooltip);
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

    public int x() {
        return this.centerX ? this.x - this.width() / 2 : this.x;
    }

    public int y() {
        return this.centerY ? this.y - this.height() / 2 : this.y;
    }

    public int endX() {
        return this.centerX ? this.x + this.width() / 2 : this.x + this.width();
    }

    public int endY() {
        return this.centerY ? this.y + this.height() / 2 : this.y + this.height();
    }

    public int middleX() {
        return this.centerX ? this.x : this.x + this.width() / 2;
    }

    public int middleY() {
        return this.centerY ? this.y : this.y + this.height() / 2;
    }

    public int width() {
        return this.width.get();
    }

    public int height() {
        return this.height.get();
    }

    public boolean contains(double x, double y) {
        return contains(x, y, this.x(), this.y(), this.width(), this.height());
    }

    @Override
    protected T clone() {
        return (T) super.clone();
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        super.changeFocus(lookForwards);

        if (this.active && this.visible) {
            this.selected ^= true;

            this.onSelection();

            return this.selected;
        }

        return false;
    }

    public void onSelection() {}

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            this.hovered = mouseX >= this.x() && mouseX <= this.endX() && mouseY >= this.y() && mouseY <= this.endY();
            this.focused = this.hovered || this.selected;

            this.renderWidget(matrices, mouseX, mouseY, delta);

            if (this.hovered) {
                this.whileHovered(matrices, mouseX, mouseY, delta);
            }

            this.wasHovered = this.hovered;
        } else {
            this.hovered = false;
        }
    }

    public void render(MatrixStack matrixes, float delta) {
        this.render(matrixes, -1, -1, delta);
    }

    public void render(MatrixStack matrixes) {
        this.render(matrixes, -1, -1, 0);
    }

    protected void renderWidget(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices, mouseX, mouseY, delta);
        this.renderForeground(matrices, mouseX, mouseY, delta);
    }

    public void renderForeground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.text != null) {
            drawTextWithShadow(matrices, textDrawer, this.text, this.middleX() - textDrawer.getWidth(this.text) / 2, this.y() + this.height() / 2 - textDrawer.fontHeight / 2, this.active ? 0xFFFFFFFF : 0xA0FFFFFF);
        }
    }

    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.tooltip != null) {
            this.tooltip.render((T) this, matrices, mouseX, mouseY);
        }
    }

    public void whileHovered(MatrixStack matrixes, int mouseX, int mouseY, float delta) {
        this.renderTooltip(matrixes, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (this.clicked(mouseX, mouseY)) {
            if (this.isValidPrimaryClick(button)) {
                this.primaryPress();

                return true;
            }

            if (this.isValidSecondaryClick(button)) {
                this.secondaryPress();

                return true;
            }

            if (this.isValidTertiaryClick(button)) {
                this.tertiaryPress();

                return true;
            }
        }

        return false;
    }

    protected boolean clicked(double mouseX, double mouseY) {
        return this.active && this.hovered;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!super.keyPressed(keyCode, scanCode, modifiers) && this.selected) {
            if (this.isValidTertiaryKey(keyCode, scanCode, modifiers)) {
                this.tertiaryPress();
            } else if (this.isValidSecondaryKey(keyCode, scanCode, modifiers)) {
                this.secondaryPress();
            } else if (this.isValidPrimaryKey(keyCode, scanCode, modifiers)) {
                this.primaryPress();
            } else {
                return false;
            }

            return true;
        }

        return false;
    }

    public boolean isValidPrimaryClick(int button) {
        return this.primaryAction != null && button == 0;
    }

    public boolean isValidSecondaryClick(int button) {
        return this.secondaryAction != null && button == 1;
    }

    public boolean isValidTertiaryClick(int button) {
        return this.tertiaryAction != null && button == 2;
    }

    private boolean isValidSecondaryKey(int keyCode, int scanCode, int modifiers) {
        return this.secondaryAction != null && this.isValidKey(keyCode, scanCode, modifiers) && (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
    }

    private boolean isValidTertiaryKey(int keyCode, int scanCode, int modifiers) {
        return this.tertiaryAction != null && this.isValidKey(keyCode, scanCode, modifiers) && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;
    }

    public boolean isValidPrimaryKey(int keyCode, int scanCode, int modifiers) {
        return this.primaryAction != null && this.isValidKey(keyCode, scanCode, modifiers);
    }

    public boolean isValidKey(int keyCode, int scanCode, int modifiers) {
        return keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER;
    }

    protected void press() {
        this.playSound();
    }

    protected void primaryPress() {
        this.press();

        this.primaryAction.onPress((T) this);
    }

    protected void secondaryPress() {
        this.press();

        this.secondaryAction.onPress((T) this);
    }

    protected void tertiaryPress() {
        this.press();

        this.tertiaryAction.onPress((T) this);
    }

    public void playSound() {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
    }
}
