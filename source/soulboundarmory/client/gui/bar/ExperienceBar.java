package soulboundarmory.client.gui.bar;

import cell.client.gui.widget.scalable.ScalableWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import soulboundarmory.client.texture.ExperienceBarTexture;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.item.SoulboundItem;

public class ExperienceBar extends ScalableWidget {
    protected static final Configuration.Client configuration = Configuration.instance().client;
    protected static final Configuration.Client.Colors colors = configuration.colors;

    protected ItemStack itemStack;
    protected ItemComponent<?> storage;

    protected int row;
    protected int length;

    {
        this.experienceBar().texture(ExperienceBarTexture.instance);
    }

    public ExperienceBar() {}

    public ExperienceBar(ItemStack itemStack) {
        this.update(itemStack);
    }

    public ExperienceBar(ItemComponent<?> storage) {
        this.update(storage);
    }

    public void data(int row, int length) {
        this.row = row;
        this.length = length;
    }

    public void drawTooltip(MatrixStack stack, ItemStack itemStack, int x, int y, int width) {
        if (this.update(itemStack)) {
            this.x(x + 4).y(y + this.row * 10).width(width - 8);
            this.render(stack);
        }
    }

    private boolean update(ItemStack itemStack) {
        if (this.update(ItemComponent.get(minecraft.player, itemStack).orElse(null))) {
            if (itemStack.getItem() instanceof SoulboundItem && this.itemStack != itemStack) {
                this.itemStack = itemStack;
            }
        }

        return false;
    }

    private boolean update(ItemComponent<?> component) {
        return (this.storage = component) != null;
    }

    @Override
    public void render(MatrixStack matrixes, int mouseX, int mouseY, float delta) {
        if (colors.alpha > 3) {
            var color = new Color(colors.red, colors.green, colors.blue, colors.alpha);
            var components = color.getComponents(null);
            var style = configuration.style;

            if (style == null) {
                style = BarStyle.EXPERIENCE;
            }

            this.v(style.v).widthLimit(1F).color4f(components[0], components[1], components[2], components[3]);
            super.render(matrixes, mouseX, mouseY, delta);

            if (this.storage.canLevelUp()) {
                this.widthLimit(Math.min(1, this.storage.floatValue(StatisticType.experience) / this.storage.nextLevelXP()));
            }

            this.v(style.v + 5);
            super.render(matrixes, mouseX, mouseY, delta);

            var level = this.storage.intValue(StatisticType.level);

            if (level > 0) {
                drawStrokedText(matrixes, String.valueOf(level), this.middleX() - textDrawer.getWidth(String.valueOf(level)) / 2F, this.y() - 8, color.getRGB());
            }

            RenderSystem.disableLighting();
        }
    }

    public boolean render(MatrixStack matrixes, Window window) {
        var component = ItemComponent.firstHeld(minecraft.player);

        if (component.isPresent()) {
            this.update(component.get());
            this.x(window.getScaledWidth() / 2).y(window.getScaledHeight() - 27).render(matrixes);

            return true;
        }

        return false;
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
