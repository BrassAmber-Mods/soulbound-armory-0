package soulboundarmory.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.config.Configuration;
import soulboundarmory.module.gui.widget.ScalableWidget;

import static org.lwjgl.opengl.GL11C.*;

public class SelectionEntryWidget extends ScalableWidget<SelectionEntryWidget> {
	private static final int SIZE = 64;

	private final ItemComponent<?> item;
	private final MasterComponent<?> component;
	private final boolean icon = Configuration.Client.selectionEntryType == Type.ICON;

	public SelectionEntryWidget(ItemComponent<?> item) {
		this.item = item;
		this.component = item.component;

		if (this.icon) {
			this.spikedRectangle(item.isUnlocked() ? 0 : 1)
				.size(26, 26)
				.view(SIZE, SIZE);
		} else {
			this.button()
				.size(128, 20)
				.text(item.name());
		}
	}

	@Override protected void render() {
		MinecraftClient.getInstance().getFramebuffer().enableStencil();
		glEnable(GL_STENCIL_TEST);
		RenderSystem.stencilMask(~0);
		RenderSystem.clear(GL_STENCIL_BUFFER_BIT, false);
		RenderSystem.stencilFunc(GL_ALWAYS, 1, 0xFF);
		RenderSystem.stencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);

		var x = this.absoluteX();
		var y = this.absoluteY();

		if (this.icon) {
			this.scale(this.matrixes, x, y);
		}

		super.render();

		if (this.icon) {
			this.renderItem(x, y);
		}

		RenderSystem.disableDepthTest();
		RenderSystem.stencilFunc(GL_EQUAL, 1, 0xFF);
		RenderSystem.stencilMask(0);
		setPositionColorShader();

		if (!this.isActive()) {
			y = this.component.item() == this.item ? y : (int) (y + this.height() * (1 - this.component.cooldown() / 600F));
			fill(this.matrixes, x, y, this.absoluteEndX(), this.absoluteEndY(), this.z(), NativeImage.packColor(95, 0, 0, 0));
		}

		if (this.icon) {
			this.matrixes.pop();
		}

		glDisable(GL_STENCIL_TEST);
	}

	private void renderItem(int x, int y) {
		var matrixes = RenderSystem.getModelViewStack();
		this.scale(matrixes, x, y);
		this.renderGuiItem(this.item.item(), x + 5, y + 5);
		matrixes.pop();
		RenderSystem.applyModelViewMatrix();
	}

	@Override protected void resetColor() {
		this.color3f(1);
	}

	private void scale(MatrixStack matrixes, int x, int y) {
		var factor = (float) SIZE / this.width();
		matrixes.push();
		matrixes.scale(factor, factor, 1);
		matrixes.translate(x / factor - x, y / factor - y, 0);
	}

	public enum Type {
		ICON,
		TEXT
	}
}
