package net.auoeke.cell.client.gui.widget;

import net.minecraft.client.util.math.MatrixStack;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.text.Text;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractTextBoxWidget<T extends AbstractTextBoxWidget<T>> extends Widget<T> {
    protected static final Int2IntMap shifted = new Int2IntOpenHashMap();
    static {
        shifted.put('`', '~');
        shifted.put('1', '!');
        shifted.put('2', '@');
        shifted.put('3', '#');
        shifted.put('4', '$');
        shifted.put('5', '%');
        shifted.put('6', '^');
        shifted.put('7', '&');
        shifted.put('8', '*');
        shifted.put('9', '(');
        shifted.put('0', ')');
        shifted.put('-', '_');
        shifted.put('=', '+');
        shifted.put('<', '>');
        shifted.put(',', '<');
        shifted.put('.', '>');
        shifted.put('/', '?');
        shifted.put(';', ':');
        shifted.put('\'', '"');
        shifted.put('\\', '|');
        shifted.put('[', '{');
        shifted.put(']', '}');
    }

    protected final StringBuffer text = new StringBuffer();

    protected List<StringVisitable> lines = new ArrayList<>();

    protected int insideWidth;
    protected int textX;
    protected int textY;

    protected int caretX;
    protected int caretY;

    protected int index;
    protected int column;

    protected AbstractTextBoxWidget() {
        this.updateCaret();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        this.renderText(matrices);
        this.renderCaret(matrices);
    }

    protected void renderText(MatrixStack matrices) {
         List<StringVisitable> lines = this.lines;

        for (int i = 0, size = lines.size(); i < size; i++) {
             StringVisitable line = lines.get(i);

            textRenderer.draw(matrices, line.getString(), this.textX, this.getY(i), 0xFFFFFF);
        }
    }

    protected void renderCaret(MatrixStack matrices) {
        textRenderer.draw(matrices, Caret.UNDERSCORE.character, this.caretX, this.caretY, 0xFFFFFF);
    }

    protected int getY(int line) {
        return this.textY + line * (textRenderer.fontHeight + 3);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE:
                if (this.index > 0) {
                    if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                        this.text.deleteCharAt(--this.index);
                    } else {
                         String substring = this.text.substring(0, this.index);
                        int start = Math.max(substring.lastIndexOf(' '), substring.lastIndexOf('\n')) + 1;

                        if (start == this.index) {
                            for (int i = start; i > 0; --i) {
                                if (!substring.substring(i - 1, i).matches("[ \\n]")) {
                                    start = i;

                                    break;
                                }
                            }
                        }

                        this.text.delete(start, this.index);
                        this.index = start;
                    }
                }

                break;
            case GLFW.GLFW_KEY_DELETE:
                if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                    this.text.delete(this.index, this.index + 1);
                } else if (this.index < this.lines.get(this.column).getString().length()) {
                     String substring = this.text.substring(this.index);
                    int end = Math.min(substring.indexOf(' '), substring.indexOf('\n'));

                    if (end < 0) {
                        end = this.text.length();
                    }

                    this.text.delete(this.index, end);
                }

                break;
            case GLFW.GLFW_KEY_LEFT_SHIFT:
            case GLFW.GLFW_KEY_RIGHT_SHIFT:
            case GLFW.GLFW_KEY_LEFT_CONTROL:
            case GLFW.GLFW_KEY_RIGHT_CONTROL:
            case GLFW.GLFW_KEY_LEFT_SUPER:
            case GLFW.GLFW_KEY_RIGHT_SUPER:
            case GLFW.GLFW_KEY_LEFT_ALT:
            case GLFW.GLFW_KEY_RIGHT_ALT:
                break;
            case GLFW.GLFW_KEY_LEFT:
                if (this.index > 0) {
                    --this.index;
                }

                break;
            case GLFW.GLFW_KEY_RIGHT:
                if (this.index < this.lines.get(this.column).getString().length()) {
                    ++this.index;
                }

                break;
            case GLFW.GLFW_KEY_UP:
                if (this.column < this.lines.size()) {
                    ++this.column;
                }

                break;
            case GLFW.GLFW_KEY_DOWN:
                if (this.column > 0) {
                    --this.column;
                }

                break;
            default:
                if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
                    keyCode = shifted.getOrDefault(keyCode, keyCode);
                } else if (keyCode >= 64 && keyCode < 90) {
                    keyCode += 32;
                }

                this.text.insert(this.index++, (char) keyCode);
        }

        this.lines = textHandler.wrapLines(this.text.toString(), this.insideWidth - 4, Style.EMPTY);
        this.updateCaret();

        return false;
    }

    protected void updateCaret() {
         int size = this.lines.size();

        if (size == 0) {
            this.caretX = this.textX;
            this.caretY = this.textY;
        } else {
            this.caretX = this.textX + textRenderer.getWidth(this.lines.get(size - 1).getString().substring(0, this.index));
            this.caretY = this.getY(this.column);
        }
    }

    protected boolean isEmpty() {
        return this.text.length() == 0;
    }

    public enum Caret {
        UNDERSCORE("_");

         Text character;

        Caret(String character) {
            this(new LiteralText(character));
        }

        Caret(Text character) {
            this.character = character;
        }
    }
}
