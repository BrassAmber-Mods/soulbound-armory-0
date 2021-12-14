package soulboundarmory.client.gui.screen;

import cell.client.gui.screen.CellScreen;
import cell.client.gui.widget.Widget;
import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.scalable.ScalableWidget;
import cell.client.gui.widget.slider.SliderWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import soulboundarmory.SoulboundArmoryClient;
import soulboundarmory.client.gui.bar.BarStyle;
import soulboundarmory.client.gui.bar.ExperienceBar;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.component.statistics.StatisticType;
import soulboundarmory.config.Configuration;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

import static soulboundarmory.component.statistics.StatisticType.experience;

/**
 The main menu of this mod.
 It keeps track of 4 tabs and stores the currently open tab as its child for rendering and input event handling.
 */
public class SoulboundScreen extends CellScreen<SoulboundScreen> {
    protected static final Configuration.Client configuration = Configuration.instance().client;
    protected static final Configuration.Client.Colors colors = configuration.colors;

    protected final PlayerEntity player = minecraft.player;
    protected final List<Widget<?>> options = new ReferenceArrayList<>(5);
    protected final List<Widget<?>> sliders = new ReferenceArrayList<>(4);
    protected final SoulboundComponent<?> component;
    protected final int slot;
    protected ItemComponent<?> item;
    protected ExperienceBar xpBar;
    protected ItemStack stack;

    private final List<Widget<?>> tabButtons = new ReferenceArrayList<>();
    private final List<SoulboundTab> tabs = new ReferenceArrayList<>();
    private SoulboundTab tab;
    private Widget<?> button;

    public SoulboundScreen(SoulboundComponent<?> component, int slot) {
        this.component = component;
        this.slot = slot;
    }

    @Override
    public void initialize() {
        this.tabButtons.clear();
        this.tabs.clear();
        this.options.clear();
        this.sliders.clear();

        this.stack = this.player.inventory.getStack(this.slot);
        this.item = ItemComponent.get(this.player, this.stack).orElse(null);

        if (this.displayTabs()) {
            this.add(this.xpBar = new ExperienceBar(this.item).width(182).height(5).x(this.width() / 2).y(this.height() - 27).center().primaryAction(bar -> {
                configuration.displayOptions ^= true;
                this.refresh();
            }).tooltip((bar, matrixes, x, y) -> {
                var xp = this.item.intValue(experience);
                this.renderTooltip(x, y, this.item.canLevelUp() ? Translations.barXP.format(xp, this.item.nextLevelXP()) : Translations.barFullXP.format(xp));
            }));

            var tabs = this.item.tabs();
            this.tab = tabs.get(this.component.tab);

            for (var tab : tabs) {
                this.addTab(tab);
                var button = this.add(this.button(tab));
                this.tabButtons.add(button);

                if (tab == this.tab) {
                    this.button = button;
                }
            }

            this.tabButtons.get(this.component.tab).active(false);

            var unbind = this.item.boundSlot() == this.slot;
            var text = unbind ? Translations.guiButtonUnbind : Translations.guiButtonBind;
            var buttonWidth = Math.max(this.button.width(), textDrawer.getWidth(text) + 8);

            this.add(new ScalableWidget<>().button()
                .x(this.button.endX() - buttonWidth)
                .y(this.height() - this.height() / 16 - 20)
                .width(buttonWidth)
                .height(20)
                .text(text)
                .primaryAction(widget -> Packets.serverBindSlot.send(new ExtendedPacketBuffer(this.item).writeInt(unbind ? -1 : this.slot)))
            );

            this.tab(this.tab.index);

            if (configuration.displayOptions) {
                this.sliders.add(this.colorSlider(Translations.red, 0));
                this.sliders.add(this.colorSlider(Translations.green, 1));
                this.sliders.add(this.colorSlider(Translations.blue, 2));
                this.sliders.add(this.colorSlider(Translations.alpha, 3));

                this.options.addAll(this.sliders);
                this.options.add(this.optionButton(
                    4,
                    Translations.style.format(configuration.style.text),
                    button -> this.cycleStyle(1),
                    button -> this.cycleStyle(-1)
                ));

                this.add(this.options);
            }
        } else {
            this.addTab(this.component.selectionTab());
            this.tab(0);
        }
    }

    @Override
    public void renderWidget() {
        renderBackground(this.matrixes);
        super.renderWidget();

        if (this.displayTabs()) {
            var maxLevel = Configuration.instance().maxLevel;
            var level = this.item.intValue(StatisticType.level);

            if (maxLevel >= 0 && level > 0 && this.hoveringLevel()) {
                this.renderTooltip(Translations.barLevel.format(level, maxLevel));
            }

            RenderSystem.disableLighting();
        }
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
    public boolean shouldClose(int keyCode, int scanCode, int modifiers) {
        return super.shouldClose(keyCode, scanCode, modifiers) || modifiers == 0 && SoulboundArmoryClient.guiKeyBinding.matchesKey(keyCode, scanCode);
    }

    @Override
    public boolean shouldPause() {
        return true;
    }

    public boolean displayTabs() {
        return this.item != null;
    }

    public void refresh() {
        this.preinitialize();
        this.tab.preinitialize();
    }

    private boolean hoveringLevel() {
        var levelString = String.valueOf(this.item.intValue(StatisticType.level));
        var levelLeftX = (this.width() - textDrawer.getWidth(levelString)) / 2;
        var levelTopY = this.height() - 35;

        return contains(mouseX(), mouseY(), levelLeftX - 1, levelTopY - 1, textDrawer.getWidth(levelString) + 2, fontHeight() + 2);
    }

    private int optionX() {
        return Math.round(this.width() * 23 / 24F) - 100;
    }

    private int optionY(int row) {
        return this.height() / 16 + Math.max(this.height() / 16 * row, 30 * row);
    }

    private <T extends ScalableWidget<T>> T optionButton(int row, Text text, PressCallback<T> primaryAction, PressCallback<T> secondaryAction) {
        return new ScalableWidget<T>().button()
            .x(this.optionX())
            .y(this.optionY(row))
            .width(100)
            .height(20)
            .text(text)
            .primaryAction(primaryAction)
            .secondaryAction(secondaryAction);
    }

    private SliderWidget colorSlider(Text text, int id) {
        return new SliderWidget()
            .x(this.optionX())
            .y(this.optionY(id))
            .width(100)
            .height(20)
            .min(0)
            .max(255)
            .discrete()
            .value(colors.get(id))
            .text(text)
            .onSlide(slider -> colors.set(id, (int) slider.value()));
    }

    private ScalableWidget<?> button(SoulboundTab tab) {
        return new ScalableWidget<>().button()
            .x(this.width() / 24)
            .y(this.height() / 16 + tab.index * Math.max(this.height() / 16, 30))
            .width(Math.max(96, Math.round(this.width() / 7.5F)))
            .height(20)
            .text(tab.title)
            .primaryAction(widget -> this.tab(tab.index));
    }

    private void addTab(SoulboundTab tab) {
        this.tabs.add(tab);
        tab.index = this.tabs.size() - 1;
    }

    private void tab(int index) {
        var tab = this.tabs.get(index);

        if (this.displayTabs()) {
            this.button.active(true);
            tab.button = this.button = this.tabButtons.get(index).active(false);
        }

        this.renew(this.tab, this.tab = tab);
        tab.preinitialize();
        this.component.tab = index;
        Packets.serverTab.send(new ExtendedPacketBuffer().writeIdentifier(this.component.key().id).writeByte(index));
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
}
