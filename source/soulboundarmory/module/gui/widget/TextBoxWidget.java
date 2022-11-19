package soulboundarmory.module.gui.widget;

public class TextBoxWidget extends TextBufferWidget<TextBoxWidget> {
	@Override protected void render() {
		fill(this.matrixes, this.absoluteX() - 1, this.absoluteY() - 1, this.absoluteEndX() + 2, this.absoluteEndY() + 2, this.isFocused() ? -1 : 0xFFA0A0A0);
		fill(this.matrixes, this.absoluteX(), this.absoluteY(), this.absoluteEndX() + 1, this.absoluteEndY() + 1, 0xFF000000);

		super.render();
	}
}
