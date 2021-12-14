package cell.client.gui.screen;

import cell.client.gui.widget.Widget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public abstract class CellScreen<T extends CellScreen<T>> extends Widget<T> {
    public Text title = LiteralText.EMPTY;
    public ScreenDelegate screen;

    @Override
    public int z() {
        return this.parent.map(parent -> parent.z() + 1000).orElse(super.z());
    }

    public T title(Text title) {
        this.title = title;

        return (T) this;
    }

    public Screen asScreen() {
        return this.screen == null ? this.screen = new ScreenDelegate(this.title, this) : this.screen;
    }

    public void open() {
        minecraft.openScreen(this.asScreen());
    }

    public boolean shouldPause() {
        return false;
    }

    public boolean shouldClose(int keyCode, int scanCode, int modifiers) {
        return keyCode == GLFW.GLFW_KEY_ESCAPE;
    }

    public void close() {
        minecraft.openScreen(this.screen.parent);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (this.shouldClose(keyCode, scanCode, modifiers)) {
            this.close();

            return true;
        }

        return false;
    }
}
