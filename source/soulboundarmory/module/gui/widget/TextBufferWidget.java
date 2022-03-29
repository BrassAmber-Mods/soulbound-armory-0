package soulboundarmory.module.gui.widget;

import java.util.List;
import java.util.regex.Pattern;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.auoeke.reflect.Flags;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public abstract class TextBufferWidget<T extends TextBufferWidget<T>> extends Widget<T> {
    protected final List<StringBuffer> lines = ReferenceArrayList.of(new StringBuffer());
    protected final TextWidget text = this.add(new TextWidget().x(2).y(2));
    protected final TextWidget caret = this.add(new TextWidget().x(2).y(2).text("_").color(0xE0E0E0).present(this::isFocused));
    protected int index;
    protected int row;

    public TextBufferWidget() {
        this.update();
    }

    @Override public T parent(Widget<?> parent) {
        super.parent(parent).update();

        return (T) this;
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (this.mouseFocused) {
            this.select(this);
            this.parent().ifPresent(parent -> parent.select(this));
        }

        return false;
    }

    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        keyboard.setRepeatEvents(true);

        if (this.isFocused()) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_ENTER -> {
                    var newLine = new StringBuffer().append(this.line(), this.index, this.line().length());
                    this.line().delete(this.index, Integer.MAX_VALUE);
                    this.lines.add(++this.row, newLine);
                    this.index = 0;
                }
                case GLFW.GLFW_KEY_BACKSPACE -> {
                    if (this.index > 0) {
                        if (Flags.none(modifiers, GLFW.GLFW_MOD_CONTROL)) {
                            this.line().deleteCharAt(--this.index);
                        } else {
                            var start = this.line().substring(0, this.index).lastIndexOf(' ') + 1;

                            if (start == this.index) {
                                var matcher = Pattern.compile("\\s+").matcher(this.line());

                                if (matcher.find()) {
                                    start = matcher.start();
                                }
                            }

                            this.line().delete(start, this.index);
                            this.index = start;
                        }
                    } else if (this.row > 0) {
                        var up = this.lines.get(this.row - 1);
                        this.index = up.length();
                        up.append(this.line(), 0, this.line().length());
                        this.lines.remove(this.row--);
                    }
                }
                case GLFW.GLFW_KEY_DELETE -> {
                    if (Flags.none(modifiers, GLFW.GLFW_MOD_CONTROL)) {
                        this.line().delete(this.index, this.index + 1);
                    } else if (this.index < this.line().length()) {
                        var end = this.line().substring(this.index).indexOf(' ');
                        this.line().delete(this.index, end >= 0 ? end : Integer.MAX_VALUE);
                    }
                }
                case GLFW.GLFW_KEY_RIGHT -> this.index(this.index + 1);
                case GLFW.GLFW_KEY_LEFT -> this.index(this.index - 1);
                case GLFW.GLFW_KEY_DOWN -> this.row(this.row + 1);
                case GLFW.GLFW_KEY_UP -> this.row(this.row - 1);
                default -> {
                    return false;
                }
            }

            this.update();

            return true;
        }

        return false;
    }

    @Override public boolean charTyped(char character, int modifiers) {
        if (this.isFocused()) {
            if (!super.charTyped(character, modifiers)) {
                this.line().insert(this.index++, character);
                this.update();
            }

            return true;
        }

        return false;
    }

    @Override public boolean focusable() {
        return this.isActive();
    }

    @Override
    protected void render() {
        this.update();
    }

    protected synchronized void update() {
        this.text.clear();
        this.lines.stream()
            .map(StringBuffer::toString)
            .forEach(line -> {
                if (line.isEmpty()) {
                    this.text.text("");
                } else {
                    textHandler.wrapLines(line, this.width() - 4, Style.EMPTY, false, (style, start, end) -> this.text.text(line.substring(start, end)));
                }
            });

        var column = 0;
        var row = 0;
        var lines = textHandler.wrapLines(this.line().toString(), this.width() - 4, Style.EMPTY);

        for (row = 0; row < lines.size(); ++row) {
            var length = lines.get(row).getString().length();

            if (column + length >= this.index) {
                column = this.index - column;
                break;
            }

            column += length;
        }

        for (var line = this.row - 1; line >= 0; --line) {
            row += Math.max(1, textHandler.wrapLines(this.line().toString(), this.width() - 4, Style.EMPTY).size());
        }

        this.caret.x(2 + width(this.line().substring(0, column)));
        this.caret.y(2 + row * fontHeight());
    }

    protected boolean isEmpty() {
        return this.lines.stream().allMatch(CharSequence::isEmpty);
    }

    protected void index(int index) {
        this.index = MathHelper.clamp(index, 0, this.line().length());

        if (index < 0) {
            this.row(this.row - 1);
        } else if (index > this.line().length()) {
            this.row(this.row + 1);
        }
    }

    protected void row(int row) {
        this.row = MathHelper.clamp(row, 0, this.lines.size() - 1);
    }

    protected StringBuffer line() {
        return this.lines.get(this.row);
    }

    public enum Caret {
        UNDERSCORE("_");

        public final Text character;

        Caret(String character) {
            this(Text.of(character));
        }

        Caret(Text character) {
            this.character = character;
        }
    }
}
