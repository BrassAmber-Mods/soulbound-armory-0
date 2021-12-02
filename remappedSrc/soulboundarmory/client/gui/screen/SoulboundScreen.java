package soulboundarmory.client.gui.screen;

import cell.client.gui.CellElement;
import cell.client.gui.DrawableElement;
import cell.client.gui.screen.CellScreen;
import cell.client.gui.widget.Slider;
import cell.client.gui.widget.Widget;
import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.scalable.ScalableWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import soulboundarmory.SoulboundArmoryClient;
import soulboundarmory.client.gui.RGBASlider;
import soulboundarmory.client.gui.bar.ExperienceBarOverlay;
import soulboundarmory.client.gui.bar.Style;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.config.Configuration;
import soulboundarmory.config.Configuration.Client;
import soulboundarmory.config.Configuration.Client.Colors;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.text.Translation;

import static soulboundarmory.component.statistics.StatisticType.experience;
import static soulboundarmory.component.statistics.StatisticType.level;

import I;

/**
 * The main menu of this mod.
 * It keeps track of 4 tabs and stores the currently open tab as its child for rendering and input event handling.
 */
public class SoulboundScreen extends CellScreen {
    protected final PlayerEntity player = SoulboundArmoryClient.player();
    protected final List<DrawableElement> options = new ReferenceArrayList<>(5);
    protected final List<Slider> sliders = new ReferenceArrayList<>(4);
    protected final SoulboundComponent component;
    protected ItemStorage<?> storage;
    protected ItemStack stack;
    protected ScalableWidget xpBar;
    protected int slot;

    private final List<SoulboundTab> tabs;
    private final List<ScalableWidget> tabButtons = new ReferenceArrayList<>();

    private SoulboundTab tab;
    private ScalableWidget button;

    public SoulboundScreen(SoulboundComponent component, int currentIndex, SoulboundTab... tabs) {
        this.component = component;
        this.tabs = List.of(tabs);

        for (I index = 0; index < this.tabs.size(); index++) {
            SoulboundTab tab = this.tabs.get(index);
            tab.parent = this;
            tab.index = index;
        }

        this.tab = this.tabs.get(currentIndex);
    }

    @Override
    protected void init() {
        this.storage = this.component.menuStorage();
        this.stack = this.component.menuStorage().stack();

        for (ItemStack itemStack : this.player.getItemsHand()) {
            if (itemStack.equals(this.stack)) {
                this.stack = itemStack;

                break;
            }
        }

        super.init();

        if (this.displayTabs()) {
            if (this.tabButtons.isEmpty()) {
                for (int index = 0, size = this.tabs.size(); index < size; index++) {
                    ScalableWidget tab = this.add(this.button(this.tabs.get(index)));
                    this.tabButtons.add(tab);

                    if (index == this.storage.tab()) {
                        tab.active = false;
                    }
                }
            }

            this.button = this.button(this.tab);

            Translation text = this.slot != this.storage.boundSlot() ? Translations.menuButtonBind : Translations.menuButtonUnbind;
            I buttonWidth = Math.max(this.button.width(), this.textRenderer.getWidth(text) + 8);

            this.add(new ScalableWidget().button()
                .x(this.button.endX() - buttonWidth)
                .y(this.height - this.height / 16 - 20)
                .width(buttonWidth)
                .height(20)
                .text(text)
                .primaryAction(this.bindSlotAction())
            );

            this.add(this.xpBar = new ExperienceBarOverlay(this.storage).width(182).height(5).x(this.width / 2).y(this.height - 27).center(true));

            if (Configuration.instance().client.displayOptions) {
                if (this.options.isEmpty()) {
                    Client configuration = Configuration.instance().client;
                    Colors colors = configuration.colors;

                    this.sliders.add(this.addButton(this.colorSlider(colors.red, Translations.red, 0)));
                    this.sliders.add(this.addButton(this.colorSlider(colors.green, Translations.green, 1)));
                    this.sliders.add(this.addButton(this.colorSlider(colors.blue, Translations.blue, 2)));
                    this.sliders.add(this.addButton(this.colorSlider(colors.alpha, Translations.alpha, 3)));

                    this.options.addAll(this.sliders);
                    this.options.add(this.add(this.optionButton(
                        4,
                        Translations.style.format(configuration.style.text),
                        this.cycleStyleAction(1),
                        this.cycleStyleAction(-1)
                    )));
                }

                this.add(this.options);
            }
        }

        this.tab(this.tab.index);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        this.withZ(-500, () -> this.renderBackground(matrices));

        super.render(matrices, mouseX, mouseY, partialTicks);

        if (this.displayTabs()) {
            this.drawXPBar(matrices, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (this.hoveringBar(mouseX, mouseY)) {
            if (Configuration.instance().client.displayOptions ^= true) {
                this.add(this.options);
            } else {
                this.remove(this.options);
            }

            this.refresh();

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double d) {
        Slider slider = this.sliderMousedOver(x, y);

        if (slider != null) {
            slider.scroll(d);

            return true;
        }

        if (d != 0) {
            I index = MathHelper.clamp((int) (this.tab.index - d), 0, this.tabs.size() - 1);

            if (index != this.tab.index) {
                this.tab(index);

                return true;
            }
        }

        return super.mouseScrolled(x, y, d);
    }

    protected Slider sliderMousedOver(double x, double y) {
        for (Slider slider : this.sliders) {
            if (CellElement.contains(x, y, slider.x, slider.y, slider.getWidth(), slider.getHeight())) {
                return slider;
            }
        }

/*
        for (var slider = 0; slider < 4; slider++) {
            if (x >= this.optionX() && x <= this.optionX() + 100
                && y >= this.optionY(slider) && y <= this.optionY(slider) + 20) {
                return this.sliders.get(slider);
            }
        }
*/

        return null;
    }

    protected void drawXPBar(MatrixStack stack, int mouseX, int mouseY) {
        I xp = this.storage.datum(experience);
        I maxLevel = Configuration.instance().maxLevel;

        if (this.hoveringLevel(mouseX, mouseY) && maxLevel >= 0) {
            this.renderTooltip(stack, Translations.barLevel.format(this.storage.datum(level), maxLevel), mouseX, mouseY);
        } else if (this.hoveringBar(mouseX, mouseY)) {
            this.renderTooltip(
                stack,
                this.storage.canLevelUp() ? Translations.barXP.format(xp, this.storage.nextLevelXP()) : Translations.barFullXP.format(xp),
                mouseX,
                mouseY
            );
        }

        RenderSystem.disableLighting();
    }

    protected boolean hoveringBar(double mouseX, double mouseY) {
        return this.displayTabs() && this.xpBar.contains(mouseX, mouseY);
    }

    protected boolean hoveringLevel(int mouseX, int mouseY) {
        String levelString = String.valueOf(this.storage.datum(level));
        I levelLeftX = (this.width - this.textRenderer.getWidth(levelString)) / 2;
        I levelTopY = this.height - 35;

        return CellElement.contains(mouseX, mouseY, levelLeftX, levelTopY, this.textRenderer.getWidth(levelString), this.textRenderer.fontHeight);
    }

    public ScalableWidget optionButton(int row, Text text, PressCallback<ScalableWidget> primaryAction, PressCallback<ScalableWidget> secondaryAction) {
        return new ScalableWidget().button()
            .x(this.optionX())
            .y(this.optionY(row))
            .width(100)
            .height(20)
            .text(text)
            .primaryAction(primaryAction)
            .secondaryAction(secondaryAction);
    }

    public Slider colorSlider(double currentValue, Text text, int id) {
        return new RGBASlider(id, text)
            .x(this.optionX())
            .y(this.optionY(id))
            .width(100)
            .height(20)
            .value(currentValue);
    }

    public int optionX() {
        return Math.round(this.width * 23 / 24F) - 100;
    }

    public int optionY(int row) {
        return this.height / 16 + Math.max(this.height / 16 * row, 30 * row);
    }

    private ScalableWidget button(ScreenTab tab) {
        return new ScalableWidget().button()
            .text(tab.label())
            .x(this.width / 24)
            .y(this.height / 16 + tab.index * Math.max(this.height / 16, 30))
            .width(Math.max(96, Math.round(this.width / 7.5F)))
            .height(20)
            .primaryAction(this.setTabAction(tab.index));
    }

    public void refresh() {
        this.tabButtons.clear();

        this.init(this.client, this.width, this.height);
        this.tab.init(this.client, this.width, this.height);
    }

    public boolean displayTabs() {
        return this.slot > -1 && this.storage.isUnlocked();
    }

    @SuppressWarnings("UnusedAssignment")
    private void tab(int tab) {
        this.remove(this.tab);
        this.tab = this.tabs.get(tab);

        this.button.active = true;
        this.tab.button = this.button = this.tabButtons.get(tab);
        this.button.active = false;

        this.tab.open(this.width, this.height);
        this.add(this.tab);
        this.storage.tab(tab);
    }

    private void cycleStyle(int change) {
        I index = (Configuration.instance().client.style.ordinal() + change) % Style.count;

        if (index < 0) {
            this.cycleStyle(Style.count + index);
        } else {
            Configuration.instance().client.style = Style.styles.get(index);
            this.refresh();
        }
    }

    private PressCallback<ScalableWidget> cycleStyleAction(int change) {
        return button -> this.cycleStyle(change);
    }

    private PressCallback<ScalableWidget> bindSlotAction() {
        return button -> Packets.serverBindSlot.send(new ExtendedPacketBuffer(this.storage).writeInt(this.slot));
    }

    private <T extends Widget<T>> PressCallback<T> setTabAction(int index) {
        return button -> this.tab(index);
    }
}
