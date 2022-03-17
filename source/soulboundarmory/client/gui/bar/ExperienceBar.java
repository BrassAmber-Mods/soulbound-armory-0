package soulboundarmory.client.gui.bar;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.client.texture.ExperienceBarTexture;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.config.Configuration;
import soulboundarmory.lib.gui.coordinate.Coordinate;
import soulboundarmory.lib.gui.widget.TextWidget;
import soulboundarmory.lib.gui.widget.TooltipWidget;
import soulboundarmory.lib.gui.widget.scalable.ScalableWidget;

public class ExperienceBar extends ScalableWidget<ExperienceBar> implements TooltipComponent {
    protected static final Configuration.Client configuration = Configuration.instance().client;
    protected static final Configuration.Client.Color color = configuration.color;

    public static final ExperienceBar overlayBar = new ExperienceBar().center().x(.5).y(1D, -27);

    protected final TextWidget level = this.add(new TextWidget())
        .x(.5)
        .centerX()
        .y(3)
        .y(Coordinate.Position.END)
        .color(color::argb)
        .stroke()
        .text(() -> this.component.level())
        .visible(() -> this.component.level() > 0)
        .tooltip(new TooltipWidget().y(-10).with(new TextWidget().text(() -> Translations.barLevel.format(this.component.level(), this.component.maxLevel() < 0 ? "∞" : this.component.maxLevel()))));

    protected ItemComponent<?> component;

    public ExperienceBar() {
        this.experienceBar().texture(ExperienceBarTexture.instance).width(182).height(5);
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.isVisible() ? this.width() + 8 : 0;
    }

    @Override
    public int getHeight() {
        return this.isVisible() ? this.height() + this.offset() + 6 : 0;
    }

    public ExperienceBar item(ItemComponent<?> component) {
        this.component = component;

        return this;
    }

    public boolean renderOverlay(MatrixStack matrixes) {
        if (this.item(ItemComponent.fromHands(client.player).orElse(null)).component != null) {
            this.render(matrixes);

            return this.isVisible();
        }

        return false;
    }

    @Override
    public boolean isVisible() {
        return color.alpha > 25 && super.isVisible();
    }

    @Override
    public void render() {
        this.v(configuration.style.v).widthLimit(1F).color4f(color.getf(0), color.getf(1), color.getf(2), color.getf(3));
        super.render();

        if (this.component.canLevelUp()) {
            this.widthLimit(Math.min(1, this.component.experience() / this.component.nextLevelXP()));
        }

        this.v(configuration.style.v + 5);
        super.render();
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrixes, ItemRenderer itemRenderer, int z) {
        this.x(x + 4).y(y + 2 + this.offset()).z(z).render(matrixes);
    }

    private int offset() {
        return this.component.level() > 0 ? fontHeight() - 2 : 0;
    }
}
