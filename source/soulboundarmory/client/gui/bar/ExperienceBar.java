package soulboundarmory.client.gui.bar;

import cell.client.gui.widget.scalable.ScalableWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import soulboundarmory.client.texture.ExperienceBarTexture;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.config.Configuration;

public class ExperienceBar extends ScalableWidget<ExperienceBar> implements TooltipComponent {
    public static final ExperienceBar overlayBar = new ExperienceBar().center();

    protected static final Configuration.Client configuration = Configuration.instance().client;
    protected static final Configuration.Client.Colors colors = configuration.colors;

    // protected final TextWidget level = new TextWidget().stroke();
    protected ItemComponent<?> component;

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.visible() ? this.width() + 8 : 0;
    }

    @Override
    public int getHeight() {
        return this.visible() ? this.height() + this.offset() : 0;
    }

    {
        this.experienceBar().texture(ExperienceBarTexture.instance).width(182).height(5);
    }

    public ExperienceBar item(ItemComponent<?> component) {
        this.component = component;

        return this;
    }

    public boolean renderOverlay(MatrixStack matrixes) {
        if (this.item(ItemComponent.fromHands(client.player).orElse(null)).component != null) {
            this.x(window.getScaledWidth() / 2).y(window.getScaledHeight() - 27).render(matrixes);

            return true;
        }

        return false;
    }

    @Override
    public boolean visible() {
        return super.visible() && colors.alpha > 25;
    }

    @Override
    public void render() {
        this.v(configuration.style.v).widthLimit(1F).color4f(colors.getf(0), colors.getf(1), colors.getf(2), colors.getf(3));
        super.render();

        if (this.component.canLevelUp()) {
            this.widthLimit(Math.min(1, this.component.experience() / this.component.nextLevelXP()));
        }

        this.v(configuration.style.v + 5);
        super.render();
        this.drawLevel();
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        var level = this.component.level();

        if (level > 0 && this.visible()) {
            var text = Text.of(String.valueOf(level)).asOrderedText();
            textRenderer.drawWithOutline(text, this.x(x + 4).middleX() - width(text), y + 3 + this.offset() - fontHeight(), colors.argb(), 0xFF000000, matrix, vertexConsumers, 0xF000F0);
        }
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrixes, ItemRenderer itemRenderer, int z) {
        this.z(z).x(x + 4).y(y + 3 + this.offset()).render(matrixes);
    }

    private void drawLevel() {
        var level = this.component.level();

        if (level > 0) {
            var text = String.valueOf(level);
            drawStrokedText(this.matrixes, text, this.middleX() - width(text) / 2F, this.y() - 6, colors.argb(), colors.alpha << 24);
        }
    }

    private int offset() {
        return this.component.level() > 0 ? fontHeight() - 2 : 0;
    }
}
