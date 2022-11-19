package soulboundarmory.module.gui.widget.slider;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import soulboundarmory.module.gui.widget.ScalableWidget;
import soulboundarmory.module.gui.widget.Widget;

public class Slider extends ScalableWidget<Slider> {
	public SlideCallback onSlide;

	public double progress;
	public boolean discrete;

	protected double min;
	protected double max;
	protected double value;
	protected double remainder;

	public Slider() {
		this.button().width(8).height(20);
	}

	public Slider min(double min) {
		this.min = min;

		return this;
	}

	public Slider max(double max) {
		this.max = max;

		return this;
	}

	public Slider discrete(boolean discrete) {
		this.discrete = discrete;

		return this;
	}

	public Slider discrete() {
		return this.discrete(true);
	}

	public Slider slide(SlideCallback callback) {
		this.onSlide = this.onSlide == null ? callback : this.onSlide.then(callback);

		return this;
	}

	public double min() {
		return this.min;
	}

	public double max() {
		return this.max;
	}

	public double range() {
		return this.max - this.min;
	}

	@Override
	public void scroll(double amount) {
		super.scroll(amount);

		var addition = Screen.hasShiftDown() ? 0.05 * this.range()
			: Screen.hasControlDown() ? 1
			: 0.01 * this.range();

		if (this.discrete) {
			var newAddition = Math.ceil(Math.abs(addition)) + (long) this.remainder;
			this.remainder += addition - newAddition;
			addition = newAddition;
		}

		this.value(MathHelper.clamp(this.value + Math.signum(amount) * addition, this.min, this.max));
	}

	public double value() {
		return this.value;
	}

	public Slider value(double value) {
		this.value = value;

		return this.progress((value - this.min) / this.range());
	}

	public Slider progress(double progress) {
		this.progress = progress;
		this.value = this.min + progress * this.range();
		this.x((int) (this.progress * this.maxX()));

		if (this.onSlide != null) {
			this.onSlide.onSlide(this);
		}

		return this;
	}

	@Override
	public boolean focusable() {
		return this.isActive();
	}

	@Override
	public boolean isFocused() {
		return super.isFocused() || this.owner().isHovered();
	}

	@Override
	public void drag() {
		this.progress(MathHelper.clamp(mouseX() - this.owner().absoluteX(), 0D, this.maxX()) / this.maxX());
	}

	@Override
	public boolean isValidPrimaryClick(int button) {
		return button == 0;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (super.mouseScrolled(mouseX, mouseY, amount)) {
			return true;
		}

		if (this.clicked()) {
			this.scroll(amount);

			return true;
		}

		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}

		if (this.isSelected()) {
			switch (keyCode) {
				case GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT -> this.scroll(-1);
				case GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_RIGHT -> this.scroll(1);
				default -> {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	@Override
	protected void primaryClick() {
		super.primaryClick();
		this.drag();
	}

	@Override
	protected boolean clicked() {
		return this.owner().isHovered() || this.isHovered();
	}

	protected Widget<?> owner() {
		return this.parent.get();
	}

	protected int maxX() {
		return this.owner().width() - this.width();
	}
}
