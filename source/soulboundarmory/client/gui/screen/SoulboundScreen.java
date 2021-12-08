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
import soulboundarmory.client.gui.bar.BarStyle;
import soulboundarmory.client.gui.bar.ExperienceBar;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemStorage;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

import static soulboundarmory.component.statistics.StatisticType.experience;
import static soulboundarmory.component.statistics.StatisticType.level;

/**
 The main menu of this mod.
 It keeps track of 4 tabs and stores the currently open tab as its child for rendering and input event handling.
 */
public class SoulboundScreen extends CellScreen {
    protected final PlayerEntity player = minecraft.player;
    protected final List<DrawableElement> options = new ReferenceArrayList<>(5);
    protected final List<Slider> sliders = new ReferenceArrayList<>(4);
    protected final SoulboundComponent component;
    protected final int slot;
    protected ItemStorage<?> storage;
    protected ScalableWidget xpBar;
    protected ItemStack stack;

    private final List<ScalableWidget> tabButtons = new ReferenceArrayList<>();
    private final List<SoulboundTab> tabs = new ReferenceArrayList<>();
    private SoulboundTab tab;
    private ScalableWidget button;

    public SoulboundScreen(SoulboundComponent component, int slot) {
        this.component = component;
        this.slot = slot;
    }

    @Override
    protected void init() {
        this.tabButtons.clear();
        this.tabs.clear();
        this.options.clear();
        this.sliders.clear();

        this.stack = this.player.inventory.getStack(this.slot);
        this.storage = ItemStorage.get(this.player, this.stack).orElse(null);

        if (this.displayTabs()) {
            var tabs = this.storage.tabs();
            this.tab = tabs.get(this.component.tab());
            this.button = this.button(this.tab);

            for (var tab : tabs) {
                this.addTab(tab);
                this.tabButtons.add(this.add(this.button(tab)));
            }

            this.tabButtons.get(this.component.tab()).active = false;

            var text = this.storage.boundSlot() == -1 ? Translations.guiButtonBind : Translations.guiButtonUnbind;
            var buttonWidth = Math.max(this.button.width(), this.textRenderer.getWidth(text) + 8);

            this.add(new ScalableWidget().button()
                .x(this.button.endX() - buttonWidth)
                .y(this.height - this.height / 16 - 20)
                .width(buttonWidth)
                .height(20)
                .text(text)
                .primaryAction(this.bindSlotAction())
            );

            this.add(this.xpBar = new ExperienceBar(this.storage).width(182).height(5).x(this.width / 2).y(this.height - 27).center(true).primaryAction(bar -> {
                if (Configuration.instance().client.displayOptions ^= true) {
                    this.add(this.options);
                } else {
                    this.remove(this.options);
                }

                this.refresh();
            }).tooltip((widget, matrixes, x, y) -> {
                var xp = this.storage.intValue(experience);
                this.renderTooltip(
                    matrixes,
                    this.storage.canLevelUp() ? Translations.barXP.format(xp, this.storage.nextLevelXP()) : Translations.barFullXP.format(xp),
                    x,
                    y
                );
            }));

            if (Configuration.instance().client.displayOptions) {
                var configuration = Configuration.instance().client;
                var colors = configuration.colors;

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

                this.add(this.options);
            }

            this.tab(this.tab.index);
        } else {
            this.tabs.clear();
            this.addTab(this.component.selectionTab());
            this.tab(0);
        }
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
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double d) {
        if (super.mouseScrolled(x, y, d)) {
            return true;
        }

        if (d != 0) {
            var index = MathHelper.clamp((int) (this.tab.index - d), 0, this.tabs.size() - 1);

            if (index != this.tab.index) {
                this.tab(index);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (modifiers == 0 && SoulboundArmoryClient.guiKeyBinding.matchesKey(keyCode, scanCode)) {
            this.onClose();

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected void drawXPBar(MatrixStack stack, int mouseX, int mouseY) {
        var maxLevel = Configuration.instance().maxLevel;
        var level = this.storage.intValue(StatisticType.level);

        if (maxLevel >= 0 && level > 0 && this.hoveringLevel(mouseX, mouseY)) {
            this.renderTooltip(stack, Translations.barLevel.format(level, maxLevel), mouseX, mouseY);
        }

        RenderSystem.disableLighting();
    }

    protected boolean hoveringLevel(int mouseX, int mouseY) {
        var levelString = String.valueOf(this.storage.intValue(level));
        var levelLeftX = (this.width - this.textRenderer.getWidth(levelString)) / 2;
        var levelTopY = this.height - 35;

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

    private void addTab(SoulboundTab tab) {
        this.tabs.add(tab);
        tab.parent = this;
        tab.index = this.tabs.size() - 1;
    }

    public boolean displayTabs() {
        return this.storage != null;
    }

    @SuppressWarnings("UnusedAssignment")
    private void tab(int tab) {
        this.remove(this.tab);
        this.tab = this.tabs.get(tab);

        if (this.displayTabs()) {
            this.button.active = true;
            this.tab.button = this.button = this.tabButtons.get(tab);
            this.button.active = false;
        }

        this.tab.open(this.width, this.height);
        this.add(this.tab);
        this.component.tab(tab);
    }

    public void refresh() {
        this.tabButtons.clear();

        this.init(this.client, this.width, this.height);
        this.tab.init(this.client, this.width, this.height);
    }

    private void cycleStyle(int change) {
        var index = (Configuration.instance().client.style.ordinal() + change) % BarStyle.count;

        if (index < 0) {
            this.cycleStyle(BarStyle.count + index);
        } else {
            Configuration.instance().client.style = BarStyle.styles.get(index);
            this.refresh();
        }
    }

    private PressCallback<ScalableWidget> cycleStyleAction(int change) {
        return button -> this.cycleStyle(change);
    }

    private PressCallback<ScalableWidget> bindSlotAction() {
        return button -> {
            Packets.serverBindSlot.send(new ExtendedPacketBuffer(this.storage).writeInt(this.slot));
            this.storage.bindSlot(this.slot);
        };
    }

    private <T extends Widget<T>> PressCallback<T> setTabAction(int index) {
        return button -> this.tab(index);
    }
}
