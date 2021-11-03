package net.auoeke.soulboundarmory.client.gui.bar;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.auoeke.soulboundarmory.capability.statistics.StatisticType;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.auoeke.cell.client.gui.widget.scalable.ScalableWidget;
import net.auoeke.soulboundarmory.SoulboundArmoryClient;
import net.auoeke.soulboundarmory.capability.soulbound.item.ItemStorage;
import net.auoeke.soulboundarmory.capability.soulbound.item.StorageType;
import net.auoeke.soulboundarmory.client.texture.ExperienceBarTexture;
import net.auoeke.soulboundarmory.config.Configuration;
import net.auoeke.soulboundarmory.item.SoulboundItem;
import net.auoeke.soulboundarmory.util.ItemUtil;

@OnlyIn(Dist.CLIENT)
public class ExperienceBarOverlay extends ScalableWidget {
    protected static final Configuration.Client configuration = Configuration.instance().client;
    protected static final Configuration.Client.Colors colors = configuration.colors;

    protected ItemStack itemStack;
    protected ItemStorage<?> component;

    protected int row;
    protected int length;

    public ExperienceBarOverlay() {
        this.experienceBar().texture(ExperienceBarTexture.instance);
    }

    public ExperienceBarOverlay(ItemStack itemStack) {
        this();

        this.update(itemStack);
    }

    public ExperienceBarOverlay(ItemStorage<?> component) {
        this();

        this.update(component);
        this.component = component;
    }

    public void data(int row, int length) {
        this.row = row;
        this.length = length;
    }

    public void drawTooltip(MatrixStack stack, int tooltipX, int tooltipY, ItemStack itemStack) {
        if (this.update(itemStack)) {
            int x = tooltipX + 4;
            int y = tooltipY + this.row * 10;

            // this.render(stack, x, y, this.length);
        }
    }

    private boolean update(ItemStack itemStack) {
        if (this.update(ItemStorage.get(SoulboundArmoryClient.player(), itemStack.getItem()))) {
            if (itemStack.getItem() instanceof SoulboundItem && this.itemStack != itemStack) {
                this.itemStack = itemStack;
            }
        }

        return false;
    }

    private boolean update(ItemStorage<?> component) {
        if (component != null) {
            this.component = component;
        }

        return this.component != null;
    }

    @Override
    public void render(MatrixStack matrixes, int mouseX, int mouseY, float delta) {
        if (colors.alpha > 3) {
            Color color = new Color(colors.red, colors.green, colors.blue, colors.alpha);
            float[] components = color.getComponents(null);
            Style style = configuration.style;

            if (style == null) {
                style = Style.EXPERIENCE;
            }

            this.v(style.v).widthLimit(1F).color4f(components[0], components[1], components[2], components[3]);
            super.render(matrixes, mouseX, mouseY, delta);

            if (this.component.canLevelUp()) {
                this.widthLimit(Math.min(1, this.component.statistic(StatisticType.experience).floatValue() / this.component.nextLevelXP()));
            }

            this.v(style.v + 5);
            super.render(matrixes, mouseX, mouseY, delta);

            int level = this.component.datum(StatisticType.level);

            if (level > 0) {
                String levelString = String.valueOf(level);
                int levelX = this.x() - textRenderer.getWidth(levelString) / 2;
                int levelY = this.y() - 8;

                textRenderer.draw(matrixes, levelString, levelX + 1, levelY, 0);
                textRenderer.draw(matrixes, levelString, levelX - 1, levelY, 0);
                textRenderer.draw(matrixes, levelString, levelX, levelY + 1, 0);
                textRenderer.draw(matrixes, levelString, levelX, levelY - 1, 0);
                textRenderer.draw(matrixes, levelString, levelX, levelY, color.getRGB());
            }

            RenderSystem.disableLighting();
        }
    }

    public boolean render(MatrixStack matrixes, Window window) {
        PlayerEntity player = SoulboundArmoryClient.player();

        return ItemUtil.handItems(player).stream().filter(item -> this.update(StorageType.get(player, item))).findAny().map(stack -> {
            this.x(window.getScaledWidth() / 2).y(window.getScaledHeight() - 27).render(matrixes);

            return stack;
        }).isPresent();
    }

    private void render(MatrixStack stack, int x, int y, int width) {
        if (colors.alpha > 3) {
            Color color = new Color(colors.red, colors.green, colors.blue, colors.alpha);
            float[] components = color.getComponents(null);
            Style style = configuration.style;

            if (style == null) {
                style = Style.EXPERIENCE;
            }

            this.x(x).y(y).v(style.v).widthLimit(1F).color4f(components[0], components[1], components[2], components[3]).render(stack);
            this.v(style.v + 5).widthLimit(this.component.canLevelUp() ? Math.min(1, this.component.statistic(StatisticType.experience).floatValue() / this.component.nextLevelXP()) : width).render(stack);

            int level = this.component.datum(StatisticType.level);

            if (level > 0) {
                String levelString = String.valueOf(level);
                int levelX = x - textRenderer.getWidth(levelString) / 2;
                int levelY = y - 8;

                textRenderer.draw(stack, levelString, levelX + 1, levelY, 0);
                textRenderer.draw(stack, levelString, levelX - 1, levelY, 0);
                textRenderer.draw(stack, levelString, levelX, levelY + 1, 0);
                textRenderer.draw(stack, levelString, levelX, levelY - 1, 0);
                textRenderer.draw(stack, levelString, levelX, levelY, color.getRGB());
            }

            RenderSystem.disableLighting();
        }
    }
}
