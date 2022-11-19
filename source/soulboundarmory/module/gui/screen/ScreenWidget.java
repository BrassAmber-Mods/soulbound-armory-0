package soulboundarmory.module.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.module.gui.widget.Widget;

public abstract class ScreenWidget<T extends ScreenWidget<T>> extends Widget<T> {
	public Text title = Translations.empty;
	public ScreenDelegate screen;

	public ScreenWidget() {
		this.z(100);
	}

	public ScreenWidget(Screen parent) {
		this();

		this.screen = new ScreenDelegate(this, parent);
	}

	public T title(Text title) {
		this.title = title;

		return (T) this;
	}

	public Screen asScreen() {
		return this.screen == null ? this.screen = new ScreenDelegate(this) : this.screen;
	}

	public void open() {
		client.setScreen(this.asScreen());
	}

	public boolean shouldPause() {
		return false;
	}

	public boolean shouldClose(int keyCode, int scanCode, int modifiers) {
		return keyCode == GLFW.GLFW_KEY_ESCAPE;
	}

	public void close() {
		client.setScreen(this.screen.parent);
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

	@Override
	protected void render() {
		this.renderBackground(this.matrixes);
	}
}
