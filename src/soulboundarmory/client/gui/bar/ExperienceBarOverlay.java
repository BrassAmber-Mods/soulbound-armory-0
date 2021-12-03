package soulboundarmory.client.gui.bar;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Optional;
import cell.client.gui.widget.scalable.ScalableWidget;
import soulboundarmory.SoulboundArmoryClient;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.item.StorageType;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.client.texture.ExperienceBarTexture;
import soulboundarmory.config.Configuration;
import soulboundarmory.item.SoulboundItem;
import soulboundarmory.util.ItemUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExperienceBarOverlay extends ScalableWidget {
    protected static final Configuration.Client configuration = Configuration.instance().client;
    protected static final Configuration.Client.Colors colors = configuration.colors;

    protected ItemStack itemStack;
    protected ItemStorage<?> storage;

    protected int row;
    protected int length;

    public ExperienceBarOverlay() {
        this.experienceBar().texture(ExperienceBarTexture.instance);
    }

    public ExperienceBarOverlay(ItemStack itemStack) {
        this();

        this.update(itemStack);
    }

    public ExperienceBarOverlay(ItemStorage<?> storage) {
        this();

        this.update(storage);
        this.storage = storage;
    }

    public void data(int row, int length) {
        this.row = row;
        this.length = length;
    }

    public void drawTooltip(MatrixStack stack, int tooltipX, int tooltipY, ItemStack itemStack) {
        if (this.update(itemStack)) {
            var x = tooltipX + 4;
            var y = tooltipY + this.row * 10;

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

    private boolean update(Optional<? extends ItemStorage<?>> storage) {
        return this.update(storage.orElse(null));
    }

    private boolean update(ItemStorage<?> component) {
        if (component != null) {
            this.storage = component;
        }

        return this.storage != null;
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
                this.widthLimit(Math.min(1, this.storage.statistic(StatisticType.experience).floatValue() / this.storage.nextLevelXP()));
            }

            this.v(style.v + 5);
            super.render(matrixes, mouseX, mouseY, delta);

            var level = this.storage.datum(StatisticType.level);

            if (level > 0) {
                var levelString = String.valueOf(level);
                var levelX = this.middleX() - textDrawer.getWidth(levelString) / 2;
                var levelY = this.y() - 8;

                textDrawer.draw(matrixes, levelString, levelX + 1, levelY, 0);
                textDrawer.draw(matrixes, levelString, levelX - 1, levelY, 0);
                textDrawer.draw(matrixes, levelString, levelX, levelY + 1, 0);
                textDrawer.draw(matrixes, levelString, levelX, levelY - 1, 0);
                textDrawer.draw(matrixes, levelString, levelX, levelY, color.getRGB());
            }

            RenderSystem.disableLighting();
        }
    }

    public boolean render(MatrixStack matrixes, Window window) {
        var player = SoulboundArmoryClient.player();
        var item = ItemUtil.handItems(player).stream().filter(i -> this.update(StorageType.get(player, i))).findAny();
        item.ifPresent(stack -> this.x(window.getScaledWidth() / 2).y(window.getScaledHeight() - 27).render(matrixes));

        return item.isPresent();
    }

    private void render(MatrixStack stack, int x, int y, int width) {
        if (colors.alpha > 3) {
            var color = new Color(colors.red, colors.green, colors.blue, colors.alpha);
            var components = color.getComponents(null);
            var style = configuration.style;

            if (style == null) {
                style = BarStyle.EXPERIENCE;
            }

            this.x(x).y(y).v(style.v).widthLimit(1F).color4f(components[0], components[1], components[2], components[3]).render(stack);
            this.v(style.v + 5).widthLimit(this.storage.canLevelUp() ? Math.min(1, this.storage.statistic(StatisticType.experience).floatValue() / this.storage.nextLevelXP()) : width).render(stack);

            var level = this.storage.datum(StatisticType.level);

            if (level > 0) {
                var levelString = String.valueOf(level);
                var levelX = x - textDrawer.getWidth(levelString) / 2;
                var levelY = y - 8;

                textDrawer.draw(stack, levelString, levelX + 1, levelY, 0);
                textDrawer.draw(stack, levelString, levelX - 1, levelY, 0);
                textDrawer.draw(stack, levelString, levelX, levelY + 1, 0);
                textDrawer.draw(stack, levelString, levelX, levelY - 1, 0);
                textDrawer.draw(stack, levelString, levelX, levelY, color.getRGB());
            }

            RenderSystem.disableLighting();
        }
    }
}
