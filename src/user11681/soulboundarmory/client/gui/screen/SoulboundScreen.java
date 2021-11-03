package user11681.soulboundarmory.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import user11681.cell.client.gui.CellElement;
import user11681.cell.client.gui.DrawableElement;
import user11681.cell.client.gui.screen.CellScreen;
import user11681.cell.client.gui.widget.Slider;
import user11681.cell.client.gui.widget.Widget;
import user11681.cell.client.gui.widget.callback.PressCallback;
import user11681.cell.client.gui.widget.scalable.ScalableWidget;
import user11681.soulboundarmory.SoulboundArmoryClient;
import user11681.soulboundarmory.capability.soulbound.item.ItemStorage;
import user11681.soulboundarmory.capability.soulbound.player.SoulboundCapability;
import user11681.soulboundarmory.client.gui.RGBASlider;
import user11681.soulboundarmory.client.gui.bar.ExperienceBarOverlay;
import user11681.soulboundarmory.client.gui.bar.Style;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;

import static user11681.soulboundarmory.capability.statistics.StatisticType.experience;
import static user11681.soulboundarmory.capability.statistics.StatisticType.level;

public class SoulboundScreen extends CellScreen {
    protected final PlayerEntity player = SoulboundArmoryClient.player();
    protected final List<DrawableElement> options = new ReferenceArrayList<>(5);
    protected final List<Slider> sliders = new ReferenceArrayList<>(4);
    protected final SoulboundCapability capability;

    protected ItemStorage<?> storage;
    protected ItemStack itemStack;
    protected ScalableWidget xpBar;

    protected int slot;

    private final List<SoulboundTab> tabs;
    private final List<ScalableWidget> buttons = new ReferenceArrayList<>();

    private SoulboundTab tab;
    private ScalableWidget button;

    public SoulboundScreen(SoulboundCapability capability, int currentIndex, SoulboundTab... tabs) {
        this.capability = capability;
        this.tabs = Arrays.asList(tabs);

        for (int index = 0; index < this.tabs.size(); index++) {
            SoulboundTab tab = this.tabs.get(index);
            tab.parent = this;
            tab.index = index;
        }

        this.tab = this.tabs.get(MathHelper.clamp(currentIndex, 0, tabs.length - 1));
    }

    @Override
    protected void init() {
        this.storage = this.capability.menuStorage();
        this.itemStack = this.capability.menuStorage().itemStack();

        for (ItemStack itemStack : this.player.getItemsHand()) {
            if (itemStack.equals(this.itemStack)) {
                this.itemStack = itemStack;

                break;
            }
        }

        super.init();

        if (this.displayTabs()) {
            if (this.buttons.isEmpty()) {
                for (int index = 0, size = this.tabs.size(); index < size; index++) {
                    ScalableWidget tab = this.add(this.button(this.tabs.get(index)));
                    this.buttons.add(tab);

                    if (index == this.storage.tab()) {
                        tab.active = false;
                    }
                }
            }

            this.button = this.button(this.tab);

            Text text = this.slot != this.storage.boundSlot() ? Translations.menuButtonBind : Translations.menuButtonUnbind;
            int buttonWidth = Math.max(this.button.width(), this.textRenderer.getWidth(text) + 8);

            this.add(new ScalableWidget().button()
                .x(this.button.endX() - buttonWidth)
                .y(this.height - this.height / 16 - 20)
                .width(buttonWidth)
                .height(20)
                .text(text)
                .primaryAction(this.bindSlotAction())
            );

            this.xpBar = new ExperienceBarOverlay(this.storage).width(182).height(5).x(this.width / 2).y(this.height - 27).center(true);
            this.add(this.xpBar);

            if (Configuration.instance().client.displayOptions) {
                if (this.options.isEmpty()) {
                    Configuration.Client configuration = Configuration.instance().client;
                    Configuration.Client.Colors colors = configuration.colors;

                    this.sliders.add(this.addButton(this.colorSlider(colors.red, Translations.red, 0)));
                    this.sliders.add(this.addButton(this.colorSlider(colors.green, Translations.green, 1)));
                    this.sliders.add(this.addButton(this.colorSlider(colors.blue, Translations.blue, 2)));
                    this.sliders.add(this.addButton(this.colorSlider(colors.alpha, Translations.alpha, 3)));

                    this.options.addAll(this.sliders);
                    this.options.add(this.add(this.optionButton(
                        4,
                        Translations.style.format(Translations.style(configuration.style)),
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
    public boolean mouseScrolled(double x, double y, double dWheel) {
        Slider slider = this.sliderMousedOver(x, y);

        if (slider != null) {
            slider.scroll(dWheel);

            return true;
        }

        if (dWheel != 0) {
            int index = MathHelper.clamp((int) (this.tab.index - dWheel), 0, this.tabs.size() - 1);

            if (index != this.tab.index) {
                this.tab(index);

                return true;
            }
        }

        return super.mouseScrolled(x, y, dWheel);
    }

    protected Slider sliderMousedOver(double mouseX, double mouseY) {
        for (int slider = 0; slider < 4; slider++) {
            if (mouseX >= this.optionX() && mouseX <= this.optionX() + 100
                && mouseY >= this.optionY(slider) && mouseY <= this.optionY(slider) + 20) {
                return this.sliders.get(slider);
            }
        }

        return null;
    }

    protected void drawXPBar(MatrixStack stack, int mouseX, int mouseY) {
        int xp = this.storage.datum(experience);
        int maxLevel = Configuration.instance().maxLevel;

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
        return this.displayTabs() && CellElement.contains(mouseX, mouseY, this.xpBar.x(), this.xpBar.x(), 182, 4);
    }

    protected boolean hoveringLevel(int mouseX, int mouseY) {
        String levelString = String.valueOf(this.storage.datum(level));

        int levelLeftX = (this.width - this.textRenderer.getWidth(levelString)) / 2;
        int levelTopY = this.height - 35;

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
        this.buttons.clear();

        this.init(this.client, this.width, this.height);
        this.tab.init(this.client, this.width, this.height);
    }

    public boolean displayTabs() {
        return this.slot > -1 && this.storage.isUnlocked();
    }

    private void tab(int tab) {
        this.remove(this.tab);
        this.tab = this.tabs.get(tab);

        this.button.active = true;
        this.button = this.buttons.get(tab);
        this.button.active = false;
        this.tab.tab = this.button;

        this.tab.open(this.width, this.height);
        this.add(this.tab);
    }

    private void cycleStyle(int change) {
        int index = (Configuration.instance().client.style.ordinal() + change) % Style.count;

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
