package soulboundarmory.client.gui.screen;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import soulboundarmory.client.gui.bar.BarStyle;
import soulboundarmory.client.gui.bar.ExperienceBar;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.client.keyboard.GUIKeyBinding;
import soulboundarmory.component.soulbound.item.ItemComponent;
import soulboundarmory.component.soulbound.player.MasterComponent;
import soulboundarmory.config.Configuration;
import soulboundarmory.lib.gui.screen.ScreenWidget;
import soulboundarmory.lib.gui.widget.Widget;
import soulboundarmory.lib.gui.widget.scalable.ScalableWidget;
import soulboundarmory.lib.gui.widget.slider.SliderWidget;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;
import soulboundarmory.util.Util;

/**
 The main menu of this mod.
 It keeps track of 4 tabs and stores the currently open tab as its child for rendering and input event handling.
 */
public class SoulboundScreen extends ScreenWidget<SoulboundScreen> {
    protected static final Configuration.Client configuration = Configuration.instance().client;
    protected static final Configuration.Client.Color color = configuration.color;

    public final ExperienceBar xpBar = new ExperienceBar()
        .x(.5)
        .y(1D, -27)
        .center()
        .tooltip(tooltip -> tooltip.text(() -> {
            var xp = (int) this.item.experience();
            return this.item.canLevelUp() ? Translations.barXP.format(xp, this.item.nextLevelXP()) : Translations.barFullXP.format(xp);
        })).primaryAction(() -> configuration.displayOptions ^= true)
        .secondaryAction(() -> configuration.overlayExperienceBar ^= true)
        .scrollAction(amount -> this.cycleStyle((int) amount))
        .present(this::displayTabs);

    protected final List<Widget<?>> options = Stream.<Widget<?>>of(
        this.colorSlider(Translations.red, 0),
        this.colorSlider(Translations.green, 1),
        this.colorSlider(Translations.blue, 2),
        this.colorSlider(Translations.alpha, 3),
        this.optionButton(4, () -> Translations.style.format(configuration.style.text), () -> this.cycleStyle(1), () -> this.cycleStyle(-1))
    ).peek(option -> option.present(() -> configuration.displayOptions && this.displayTabs())).toList();
    protected final MasterComponent<?> component;
    protected final int slot;
    protected ItemStack stack;

    private final List<SoulboundTab> tabs = ReferenceArrayList.of();
    private ItemComponent<?> item;
    private SoulboundTab tab;

    public SoulboundScreen(MasterComponent<?> component, int slot) {
        this.component = component;
        this.slot = slot;
    }

    @Override public void tick() {
        var previousItem = this.item;
        this.baseTick();

        if (previousItem != null && this.item != previousItem) {
            if (this.item == null) {
                this.close();

                return;
            }

            if (this.item.component != this.component) {
                this.close();
                this.item.component.tryOpenGUI(this.stack, this.slot);

                return;
            }

            this.refresh();
        }

        super.tick();
    }

    @Override
    public void initialize() {
        this.tabs.clear();
        this.baseTick();
        this.add(this.xpBar);

        if (this.displayTabs()) {
            this.tabs.addAll(this.item.tabs());
            this.tab = this.tabs.get(MathHelper.clamp(this.component.tab(), 0, this.tabs.size() - 1));
        } else {
            this.tabs.add(this.tab = this.component.selectionTab());
        }

        Util.enumerate(this.tabs, (tab, index) -> {
            tab.index = index;
            tab.button = this.add(this.button(tab));
        });

        this.tab(this.tab);

        this.add(new ScalableWidget<>())
            .button()
            .x(this.tab.button)
            .y(15D / 16)
            .centerY()
            .width(button -> Math.max(this.tab.button.width(), button.descendantWidth() + 12))
            .height(20)
            .text(() -> this.bound() ? Translations.guiButtonUnbind : Translations.guiButtonBind)
            .present(this::displayTabs)
            .primaryAction(() -> Packets.serverBindSlot.send(new ExtendedPacketBuffer(this.component).writeInt(this.bound() ? -1 : this.slot)));

        this.add(this.options);
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

    public ItemComponent<?> item() {
        return this.item;
    }

    public boolean displayTabs() {
        return this.item != null;
    }

    public void refresh() {
        this.preinitialize();
        this.tab.preinitialize();
    }

    private void baseTick() {
        this.stack = player().getInventory().getStack(this.slot);
        this.xpBar.item(this.item = ItemComponent.of(player(), this.stack).orElse(null));
    }

    private boolean bound() {
        return this.component.boundSlot() == this.slot;
    }

    private int optionX() {
        return Math.round(this.width() * 23 / 24F) - 100;
    }

    private int optionY(int row) {
        return this.height() / 16 + Math.max(this.height() / 16, 30) * row;
    }

    private <T extends ScalableWidget<T>> T optionButton(int row, Supplier<? extends Text> text, Runnable primaryAction, Runnable secondaryAction) {
        return new ScalableWidget<T>()
            .button()
            .x(__ -> this.optionX())
            .y(__ -> this.optionY(row))
            .width(100)
            .height(20)
            .centeredText(widget -> widget.text(text))
            .primaryAction(primaryAction)
            .secondaryAction(secondaryAction);
    }

    private SliderWidget colorSlider(Text text, int id) {
        return new SliderWidget()
            .x(__ -> this.optionX())
            .y(__ -> this.optionY(id))
            .width(100)
            .height(20)
            .min(0)
            .max(255)
            .discrete()
            .value(color.get(id))
            .text(text)
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
            .present(this::displayTabs)
            .active(() -> tab != this.tab);
    }

    private void tab(SoulboundTab tab) {
        this.renew(this.tab, this.tab = tab);
        tab.preinitialize();
        this.component.tab(tab.index);
    }

    private void cycleStyle(int change) {
        var index = (Configuration.instance().client.style.ordinal() + change) % BarStyle.count;
        Configuration.instance().client.style = BarStyle.styles.get(index >= 0 ? index : index + BarStyle.count);
    }
}
