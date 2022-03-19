package soulboundarmory.client.gui.screen;

import soulboundarmory.lib.gui.screen.CellScreen;
import soulboundarmory.lib.gui.widget.TextWidget;
import soulboundarmory.lib.gui.widget.TooltipWidget;
import soulboundarmory.lib.gui.widget.Widget;
import soulboundarmory.lib.gui.widget.scalable.ScalableWidget;
import soulboundarmory.lib.gui.widget.slider.SliderWidget;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import soulboundarmory.client.gui.bar.BarStyle;
import soulboundarmory.client.gui.bar.ExperienceBar;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.client.keyboard.GUIKeyBinding;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.player.SoulboundComponent;
import soulboundarmory.config.Configuration;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

/**
 The main menu of this mod.
 It keeps track of 4 tabs and stores the currently open tab as its child for rendering and input event handling.
 */
public class SoulboundScreen extends CellScreen<SoulboundScreen> {
    protected static final Configuration.Client configuration = Configuration.instance().client;
    protected static final Configuration.Client.Color color = configuration.color;

    public final ExperienceBar xpBar = new ExperienceBar()
        .x(.5)
        .y(1D, -27)
        .center()
        .tooltip(new TooltipWidget().with(new TextWidget().text(() -> {
            var xp = (int) this.item.experience();
            return this.item.canLevelUp() ? Translations.barXP.format(xp, this.item.nextLevelXP()) : Translations.barFullXP.format(xp);
        }))).primaryAction(() -> {
            configuration.displayOptions ^= true;
            this.refresh();
        }).secondaryAction(() -> configuration.overlayExperienceBar ^= true)
        .scrollAction(amount -> this.cycleStyle((int) amount))
        .present(this::displayTabs);

    protected final List<Widget<?>> options = new ReferenceArrayList<>(5);
    protected final List<Widget<?>> sliders = new ReferenceArrayList<>(4);
    protected final SoulboundComponent<?> component;
    protected final int slot;
    protected ItemComponent<?> item;
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
        this.add(this.xpBar);

        this.stack = player().getInventory().getStack(this.slot);
        this.item = ItemComponent.of(player(), this.stack).orElse(null);

        if (this.displayTabs()) {
            this.xpBar.item(this.item);

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

            var unbind = this.component.boundSlot() == this.slot;
            var text = unbind ? Translations.guiButtonUnbind : Translations.guiButtonBind;

            this.add(new ScalableWidget<>())
                .button()
                .x(this.button)
                .y(15D / 16, -20)
                .width(Math.max(this.button.width(), width(text) + 8))
                .height(20)
                .text(text)
                .present(this::displayTabs)
                .primaryAction(() -> Packets.serverBindSlot.send(new ExtendedPacketBuffer(this.component).writeInt(unbind ? -1 : this.slot)));

            this.tab(this.tab);

            if (configuration.displayOptions) {
                this.sliders.add(this.colorSlider(Translations.red, 0));
                this.sliders.add(this.colorSlider(Translations.green, 1));
                this.sliders.add(this.colorSlider(Translations.blue, 2));
                this.sliders.add(this.colorSlider(Translations.alpha, 3));

                this.options.addAll(this.sliders);

                this.options.add(this.optionButton(
                    4,
                    Translations.style.format(configuration.style.text),
                    () -> this.cycleStyle(1),
                    () -> this.cycleStyle(-1)
                ));

                this.add(this.options);
            }
        } else {
            var tab = this.component.selectionTab();
            this.addTab(tab);
            this.tab(tab);
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double d) {
        if (super.mouseScrolled(x, y, d)) {
            return true;
        }

        if (d != 0) {
            var tab = this.tabs.get(MathHelper.clamp((int) (this.tab.index - d), 0, this.tabs.size() - 1));

            if (tab != this.tab) {
                this.tab(tab);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean shouldClose(int keyCode, int scanCode, int modifiers) {
        return super.shouldClose(keyCode, scanCode, modifiers) || modifiers == 0 && GUIKeyBinding.instance.matchesKey(keyCode, scanCode);
    }

    public boolean displayTabs() {
        return this.item != null;
    }

    public void refresh() {
        this.preinitialize();
        this.tab.preinitialize();
    }

    private int optionX() {
        return Math.round(this.width() * 23 / 24F) - 100;
    }

    private int optionY(int row) {
        return this.height() / 16 + Math.max(this.height() / 16, 30) * row;
    }

    private <T extends ScalableWidget<T>> T optionButton(int row, Text text, Runnable primaryAction, Runnable secondaryAction) {
        return new ScalableWidget<T>()
            .button()
            .x(this.optionX())
            .y(this.optionY(row))
            .width(100)
            .height(20)
            .text(text)
            .present(this::displayTabs)
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
            .value(color.get(id))
            .text(text)
            .present(this::displayTabs)
            .onSlide(slider -> color.set(id, (int) slider.value()));
    }

    private ScalableWidget<?> button(SoulboundTab tab) {
        return new ScalableWidget<>()
            .button()
            .x(1D / 24)
            .y(this.optionY(tab.index))
            .width(Math.max(96, Math.round(this.width() / 7.5F)))
            .height(20)
            .text(tab.title)
            .primaryAction(() -> this.tab(tab))
            .active(() -> tab != this.tab);
    }

    private void addTab(SoulboundTab tab) {
        this.tabs.add(tab);
        tab.index = this.tabs.size() - 1;
    }

    private void tab(SoulboundTab tab) {
        if (this.displayTabs()) {
            tab.button = this.button = this.tabButtons.get(tab.index);
        }

        this.renew(this.tab, this.tab = tab);
        tab.preinitialize();
        this.component.tab(tab.index);
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
