package soulboundarmory.client.gui.bar;

import cell.client.gui.widget.scalable.ScalableWidget;
import java.awt.Color;
import java.util.Optional;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import soulboundarmory.client.texture.ExperienceBarTexture;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;

public class ExperienceBar extends ScalableWidget<ExperienceBar> implements TooltipComponent {
    protected static final Configuration.Client configuration = Configuration.instance().client;
    protected static final Configuration.Client.Colors colors = configuration.colors;

    protected ItemStack stack;
    protected ItemComponent<?> component;

    protected int row;
    protected int length;

    @Override
    public int getHeight() {
        return this.height();
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.width() + 8;
    }

    {
        this.experienceBar().texture(ExperienceBarTexture.instance).width(182).height(5);
    }

    public ExperienceBar() {}

    public ExperienceBar(ItemComponent<?> component) {
        this.item(component);
    }

    public void data(int row, int length) {
        this.row = row;
        this.length = length;
    }

    public boolean renderOverlay(MatrixStack matrixes) {
        if (this.item(ItemComponent.fromHands(minecraft.player))) {
            this.x(window.getScaledWidth() / 2).y(window.getScaledHeight() - 27).render(matrixes);

            return true;
        }

        return false;
    }

    @Override
    public void renderWidget() {
        if (colors.alpha > 3) {
            var color = new Color(colors.red, colors.green, colors.blue, colors.alpha);
            var components = color.getComponents(null);
            var style = configuration.style;

            this.v(style.v).widthLimit(1F).color4f(components[0], components[1], components[2], components[3]);
            super.renderWidget();

            if (this.component.canLevelUp()) {
                this.widthLimit(Math.min(1, this.component.floatValue(StatisticType.experience) / this.component.nextLevelXP()));
            }

            this.v(style.v + 5);
            super.renderWidget();

            var level = this.component.intValue(StatisticType.level);

            if (level > 0) {
                drawStrokedText(this.matrixes, String.valueOf(level), this.middleX() - textDrawer.getWidth(String.valueOf(level)) / 2F, this.y() - 8, color.getRGB());
            }

            // RenderSystem.disableLighting();
        }
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrixes, ItemRenderer itemRenderer, int z) {
        this.z(z).x(x + 4).y(y + this.row * 10).render(matrixes);
    }

    private boolean item(ItemComponent<?> component) {
        return (this.component = component) != null;
    }

    private boolean item(Optional<ItemComponent<?>> component) {
        return this.item(component.orElse(null));
    }

    /*
    private void render(MatrixStack stack) {
        if (colors.alpha > 3) {
            var color = new Color(colors.red, colors.green, colors.blue, colors.alpha);
            var components = color.getComponents(null);
            var style = configuration.style;

            if (style == null) {
                style = BarStyle.EXPERIENCE;
            }

            this.v(style.v).widthLimit(1F).color4f(components[0], components[1], components[2], components[3]).render(stack);
            this.v(style.v + 5).widthLimit(this.storage.canLevelUp() ? Math.min(1, this.storage.floatValue(StatisticType.experience) / this.storage.nextLevelXP()) : this.length).render(stack);

            var level = this.storage.intValue(StatisticType.level);

            if (level > 0) {
                var levelString = String.valueOf(level);
                var levelX = this.x() - textDrawer.getWidth(levelString) / 2;
                var levelY = this.y() - 8;

                textDrawer.draw(stack, levelString, levelX + 1, levelY, 0);
                textDrawer.draw(stack, levelString, levelX - 1, levelY, 0);
                textDrawer.draw(stack, levelString, levelX, levelY + 1, 0);
                textDrawer.draw(stack, levelString, levelX, levelY - 1, 0);
                textDrawer.draw(stack, levelString, levelX, levelY, color.getRGB());
            }

            RenderSystem.disableLighting();
        }
    }
*/
}
