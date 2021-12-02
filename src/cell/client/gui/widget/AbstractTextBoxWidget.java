package cell.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractTextBoxWidget<T extends AbstractTextBoxWidget<T>> extends Widget<T> {
    protected static final Char2CharMap shifted = new Char2CharOpenHashMap(Map.ofEntries(
        Map.entry('`', '~'),
        Map.entry('1', '!'),
        Map.entry('2', '@'),
        Map.entry('3', '#'),
        Map.entry('4', '$'),
        Map.entry('5', '%'),
        Map.entry('6', '^'),
        Map.entry('7', '&'),
        Map.entry('8', '*'),
        Map.entry('9', '('),
        Map.entry('0', ')'),
        Map.entry('-', '_'),
        Map.entry('=', '+'),
        Map.entry('<', '>'),
        Map.entry(',', '<'),
        Map.entry('.', '>'),
        Map.entry('/', '?'),
        Map.entry(';', ':'),
        Map.entry('\'', '"'),
        Map.entry('\\', '|'),
        Map.entry('[', '{'),
        Map.entry(']', '}')
    ));

    protected final StringBuffer text = new StringBuffer();

    protected List<ITextProperties> lines = new ArrayList<>();

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
        var lines = this.lines;

        for (int i = 0, size = lines.size(); i < size; i++) {
            var line = lines.get(i);
            textRenderer.drawString(matrices, line.getString(), this.textX, this.getPosY(i), 0xFFFFFF);
        }
    }

    protected void renderCaret(MatrixStack matrices) {
        textRenderer.func_243246_a(matrices, Caret.UNDERSCORE.character, this.caretX, this.caretY, 0xFFFFFF);
    }

    protected int getPosY(int line) {
        return this.textY + line * (textRenderer.FONT_HEIGHT + 3);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (this.index > 0) {
                    if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                        this.text.deleteCharAt(--this.index);
                    } else {
                        var substring = this.text.substring(0, this.index);
                        var start = Math.max(substring.lastIndexOf(' '), substring.lastIndexOf('\n')) + 1;

                        if (start == this.index) {
                            for (var i = start; i > 0; --i) {
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
            }
            case GLFW.GLFW_KEY_DELETE -> {
                if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                    this.text.delete(this.index, this.index + 1);
                } else if (this.index < this.lines.get(this.column).getString().length()) {
                    var substring = this.text.substring(this.index);
                    var end = Math.min(substring.indexOf(' '), substring.indexOf('\n'));

                    if (end < 0) {
                        end = this.text.length();
                    }

                    this.text.delete(this.index, end);
                }
            }
            case GLFW.GLFW_KEY_LEFT_SHIFT,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                GLFW.GLFW_KEY_LEFT_CONTROL,
                GLFW.GLFW_KEY_RIGHT_CONTROL,
                GLFW.GLFW_KEY_LEFT_SUPER,
                GLFW.GLFW_KEY_RIGHT_SUPER,
                GLFW.GLFW_KEY_LEFT_ALT,
                GLFW.GLFW_KEY_RIGHT_ALT -> {}
            case GLFW.GLFW_KEY_LEFT -> {
                if (this.index > 0) {
                    --this.index;
                }
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                if (this.index < this.lines.get(this.column).getString().length()) {
                    ++this.index;
                }
            }
            case GLFW.GLFW_KEY_UP -> {
                if (this.column < this.lines.size()) {
                    ++this.column;
                }
            }
            case GLFW.GLFW_KEY_DOWN -> {
                if (this.column > 0) {
                    --this.column;
                }
            }
            default -> {
                if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
                    keyCode = shifted.getOrDefault((char) keyCode, (char) keyCode);
                } else if (keyCode >= 64 && keyCode < 90) {
                    keyCode += 32;
                }

                this.text.insert(this.index++, (char) keyCode);
            }
        }

        this.lines = textHandler.func_238365_g_(this.text.toString(), this.insideWidth - 4, Style.EMPTY);
        this.updateCaret();

        return false;
    }

    protected void updateCaret() {
        var size = this.lines.size();

        if (size == 0) {
            this.caretX = this.textX;
            this.caretY = this.textY;
        } else {
            this.caretX = this.textX + textRenderer.getStringWidth(this.lines.get(size - 1).getString().substring(0, this.index));
            this.caretY = this.getPosY(this.column);
        }
    }

    protected boolean isEmpty() {
        return this.text.length() == 0;
    }

    public enum Caret {
        UNDERSCORE("_");

        ITextComponent character;

        Caret(String character) {
            this(new StringTextComponent(character));
        }

        Caret(ITextComponent character) {
            this.character = character;
        }
    }
}
