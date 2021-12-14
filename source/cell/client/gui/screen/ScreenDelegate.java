package cell.client.gui.screen;

import cell.client.gui.widget.Widget;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import soulboundarmory.util.Util;

public final class ScreenDelegate extends Screen {
    public final Screen parent = Widget.screen();
    public final CellScreen<?> screen;

    public ScreenDelegate(Text title, CellScreen<?> screen) {
        super(title);

        this.screen = screen;
    }

    @Override
    public void render(MatrixStack matrixes, int mouseX, int mouseY, float delta) {
        this.screen.render(matrixes, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.screen.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void onClose() {
        this.screen.close();
    }

    @Override
    public Text getTitle() {
        return this.screen.title;
    }

    @Override
    public List<? extends Element> children() {
        return this.screen.children;
    }

    @Override
    public Optional<Element> hoveredElement(double mouseX, double mouseY) {
        return this.screen.hoveredChild().map(Util::cast);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.screen.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.screen.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.screen.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.screen.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.screen.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.screen.charTyped(chr, modifiers);
    }

    @Override
    public void setInitialFocus(@Nullable Element element) {
        this.screen.select((Widget<?>) element);
    }

    @Override
    public void focusOn(@Nullable Element element) {
        this.screen.select((Widget<?>) element);
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return this.screen.changeFocus(lookForwards);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.screen.mouseMoved(mouseX, mouseY);
    }

    @Override
    public Element getFocused() {
        return this.screen.selected.orElse(null);
    }

    @Override
    public void setFocused(Element focused) {
        this.screen.select((Widget<?>) focused);
    }

    @Override
    public boolean isPauseScreen() {
        return this.screen.shouldPause();
    }

    @Override
    protected void init() {
        this.screen.width(this.width).height(this.height).preinitialize();
    }

    @Override
    public void tick() {
        this.screen.tick();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.screen.isMouseOver(mouseX, mouseY);
    }
}
