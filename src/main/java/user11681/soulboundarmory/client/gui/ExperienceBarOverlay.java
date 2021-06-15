package user11681.soulboundarmory.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.MainWindow;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.cell.client.gui.screen.CellScreen;
import user11681.cell.client.gui.widget.scalable.ScalableTextureInfo;
import user11681.cell.client.gui.widget.scalable.ScalableWidget;
import user11681.soulboundarmory.SoulboundArmoryClient;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.item.StorageType;
import user11681.soulboundarmory.capability.statistics.StatisticType;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.client.texture.ExperienceBarTexture;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.item.SoulboundItem;

import static user11681.soulboundarmory.capability.statistics.StatisticType.experience;

@OnlyIn(Dist.CLIENT)
public class ExperienceBarOverlay extends CellScreen {
    protected static final Configuration.Client configuration = Configuration.instance().client;
    protected static final Configuration.Client.Colors colors = configuration.colors;

    protected final ScalableWidget widget = new ScalableWidget().texture(new ScalableTextureInfo().texture(new ExperienceBarTexture()));

    protected ItemStack itemStack;

    protected ItemStorage<?> component;
    protected int row;
    protected int length;

    public ExperienceBarOverlay(ItemStack itemStack) {
        this();

        this.update(itemStack);
    }

    public ExperienceBarOverlay(ItemStorage<?> component) {
        this();

        this.update(component);

        this.component = component;
    }

    public ExperienceBarOverlay() {
        super(null);
    }

    public void setData(int row, int length) {
        this.row = row;
        this.length = length;
    }

    public void drawTooltip(int tooltipX, int tooltipY, ItemStack itemStack) {
        if (this.update(itemStack)) {
            int x = tooltipX + 4;
            int y = tooltipY + this.row * 10;

            this.render(x, y, this.length);
        }
    }

    public boolean update(ItemStack itemStack) {
        if (this.update(ItemStorage.get(SoulboundArmoryClient.player(), itemStack.getItem()))) {
            if (itemStack.getItem() instanceof SoulboundItem && this.itemStack != itemStack) {
                this.itemStack = itemStack;
            }
        }

        return false;
    }

    public boolean update(ItemStorage<?> component) {
        if (component != null) {
            this.component = component;
        }

        return this.component != null;
    }

    public void update() {

    }

    public boolean render() {
        PlayerEntity player = SoulboundArmoryClient.player();
        MainWindow window = client.getWindow();

        for (ItemStack itemStack : player.getHandSlots()) {
            if (this.update(StorageType.get(player, itemStack.getItem()))) {
                this.render((window.getGuiScaledWidth() - 182) / 2, window.getGuiScaledHeight() - 29, 182);

                return true;
            }
        }

        return false;
    }

    public void render(int x, int y, int width) {
        if (colors.alpha > 3) {
            MatrixStack stack = new MatrixStack();
            Color color = new Color(colors.red, colors.green, colors.blue, colors.alpha);
            float[] components = color.getComponents(null);
            Style style = configuration.style;

            if (style == null) {
                style = Style.EXPERIENCE;
            }

            float ratio = (float) this.component.datum(experience) / this.component.nextLevelXP();
            float effectiveWidth = ratio * width;
            int middleU = (int) Math.min(4, effectiveWidth);

            this.widget.x(x).y(y).color4f(components[0], components[1], components[2], components[3]);
            this.widget.v(style.v).render(stack);
            this.widget.v(style.v + 5).textureWidth(this.component.canLevelUp() ? Math.min(1, ratio) : width).render(stack);

            int level = this.component.datum(StatisticType.level);

            if (level > 0) {
                String levelString = String.format("%d", level);
                int levelX = x + (width - fontRenderer.width(levelString)) / 2;
                int levelY = y - 6;

                fontRenderer.draw(stack, levelString, levelX + 1, levelY, 0);
                fontRenderer.draw(stack, levelString, levelX - 1, levelY, 0);
                fontRenderer.draw(stack, levelString, levelX, levelY + 1, 0);
                fontRenderer.draw(stack, levelString, levelX, levelY - 1, 0);
                fontRenderer.draw(stack, levelString, levelX, levelY, color.getRGB());
            }

            RenderSystem.disableLighting();
        }
    }

    public enum Style {
        EXPERIENCE(64, Translations.xpStyle),
        BOSS(74, Translations.bossStyle),
        HORSE(84, Translations.horseStyle);

        public static final List<Style> styles = ReferenceArrayList.wrap(values());
        public static final int count = styles.size();

        public final int v;

        protected final ITextComponent text;

        Style(int v, ITextComponent text) {
            this.v = v;
            this.text = text;
        }

        public ITextComponent getText() {
            return this.text;
        }
    }
}
