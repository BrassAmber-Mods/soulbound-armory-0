package user11681.soulboundarmory.client.gui.screen.tab;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import user11681.cell.client.gui.screen.ScreenTab;
import user11681.cell.client.gui.widget.callback.PressCallback;
import user11681.cell.client.gui.widget.scalable.ScalableWidget;
import user11681.cell.client.gui.widget.scalable.ScalableWidgets;
import user11681.soulboundarmory.SoulboundArmoryClient;
import user11681.soulboundarmory.client.gui.ExperienceBarOverlay;
import user11681.soulboundarmory.client.gui.ExperienceBarOverlay.Style;
import user11681.soulboundarmory.client.gui.RGBASlider;
import user11681.soulboundarmory.client.i18n.Translations;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.statistics.Category;
import user11681.soulboundarmory.config.Configuration;
import user11681.soulboundarmory.network.ExtendedPacketBuffer;
import user11681.soulboundarmory.registry.Packets;
import user11681.soulboundarmory.text.StringableText;

import static user11681.soulboundarmory.component.statistics.StatisticType.experience;
import static user11681.soulboundarmory.component.statistics.StatisticType.level;

@Environment(EnvType.CLIENT)
public abstract class SoulboundTab extends ScreenTab {
    protected static final NumberFormat format = DecimalFormat.getInstance();

    protected final PlayerEntity player;
    protected final List<Element> options;
    protected final List<RGBASlider> sliders;
    protected final SoulboundComponent<?> component;

    protected ItemStorage<?> storage;
    protected ItemStack itemStack;
    protected ExperienceBarOverlay xpBar;
    protected int slot;

    public SoulboundTab(Text title, SoulboundComponent<?> component, List<ScreenTab> tabs) {
        super(title, tabs);

        this.player = SoulboundArmoryClient.getPlayer();
        this.component = component;
        this.options = new ArrayList<>(5);
        this.sliders = new ArrayList<>(4);
    }

    @Override
    public void init() {
        this.storage = this.component.menuStorage();
        this.itemStack = this.component.menuStorage().getItemStack();

        for (ItemStack itemStack : this.player.getItemsHand()) {
            if (itemStack.equals(this.itemStack)) {
                this.itemStack = itemStack;

                break;
            }
        }

        super.init();

        if (this.displayTabs()) {
            this.storage.setCurrentTab(this.index);

            Text text = this.slot != storage.getBoundSlot() ? Translations.menuButtonBind : Translations.menuButtonUnbind;
            int buttonWidth = Math.max(this.tab.width, this.textRenderer.getWidth(text) + 8);

            this.addButton(new ButtonWidget(this.tab.endX() - buttonWidth, this.height - this.height / 16 - 20, buttonWidth, 20, text, this.bindSlotAction()));

            if (this.displayXPBar()) {
                this.xpBar = new ExperienceBarOverlay(this.storage);

                this.initSettings();
            }
        }
    }

    protected boolean initSettings() {
        if (Configuration.instance().client.displayOptions) {
            if (this.options.isEmpty()) {
                Configuration.Client configuration = Configuration.instance().client;
                Configuration.Client.Colors colors = configuration.colors;

                this.sliders.add(this.addButton(this.colorSlider(colors.red, Translations.red, 0)));
                this.sliders.add(this.addButton(this.colorSlider(colors.green, Translations.green, 1)));
                this.sliders.add(this.addButton(this.colorSlider(colors.blue, Translations.blue, 2)));
                this.sliders.add(this.addButton(this.colorSlider(colors.alpha, Translations.alpha, 3)));
                this.options.add(this.add(this.optionButton(4, new StringableText("%s: %s", Translations.xpBarStyle, configuration.style), this.cycleStyleAction(1), this.cycleStyleAction(-1))));
                this.options.addAll(sliders);

                return true;
            }

            this.add(this.options);
        }

        return false;
    }

    @Override
    protected boolean displayTabs() {
        return this.slot > -1 && this.storage.isUnlocked();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        this.withZ(-500, () -> this.renderBackground(matrices));

        super.render(matrices, mouseX, mouseY, partialTicks);

        if (this.displayXPBar()) {
            this.drawXPBar(matrices, mouseX, mouseY);
        }
    }

    protected void drawXPBar(MatrixStack matrices, int mouseX, int mouseY) {
        int xp = this.storage.getDatum(experience);
        int maxLevel = Configuration.instance().maxLevel;

        this.xpBar.render();

        if (this.isMouseOverLevel(mouseX, mouseY) && maxLevel >= 0) {
            this.renderTooltip(matrices, new StringableText("%s/%s", this.storage.getDatum(level), maxLevel), mouseX, mouseY);
        } else if (this.isMouseOverXPBar(mouseX, mouseY)) {
            this.renderTooltip(matrices, this.storage.canLevelUp()
                    ? new StringableText("%s/%s", xp, this.storage.getNextLevelXP())
                    : new StringableText("%s", xp), mouseX, mouseY);
        }

//        RenderSystem.disableLighting();
    }

    protected boolean displayXPBar() {
        return this.displayTabs();
    }

    protected boolean isMouseOverXPBar(double mouseX, double mouseY) {
        double barX = this.getXPBarX();
        double barY = this.getXPBarY();

        return this.displayXPBar() && mouseX >= barX && mouseX <= barX + 182 && mouseY >= barY && mouseY <= barY + 4;
    }

    protected int getXPBarY() {
        return this.height - 29;
    }

    protected int getXPBarX() {
        return (this.width - 182) / 2;
    }

    protected boolean isMouseOverLevel(int mouseX, int mouseY) {
        String levelString = "" + this.storage.getDatum(level);

        int levelLeftX = (this.width - this.textRenderer.getWidth(levelString)) / 2;
        int levelTopY = height - 35;

        return mouseX >= levelLeftX && mouseX <= levelLeftX + this.textRenderer.getWidth(levelString)
                && mouseY >= levelTopY && mouseY <= levelTopY + this.textRenderer.fontHeight;
    }

    protected RGBASlider sliderMousedOver(double mouseX, double mouseY) {
        for (int slider = 0; slider < 4; slider++) {
            if (mouseX >= this.optionX() && mouseX <= this.optionX() + 100
                    && mouseY >= this.optionY(slider) && mouseY <= this.optionY(slider) + 20) {
                return this.sliders.get(slider);
            }
        }

        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (this.isMouseOverXPBar(mouseX, mouseY)) {
            if (Configuration.instance().client.displayOptions) {
                this.removeButtons(this.options);
                Configuration.instance().client.displayOptions = false;
            } else {
                this.add(this.options);
                Configuration.instance().client.displayOptions = true;
            }

            this.refresh();

            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (SoulboundArmoryClient.guiKeyBinding.matchesKey(keyCode, scanCode)) {
            this.onClose();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double dWheel) {
        RGBASlider slider = this.sliderMousedOver(x, y);

        if (slider != null) {
            slider.scroll(dWheel);

            return true;
        }

        return super.mouseScrolled(x, y, dWheel);
    }

    protected void cycleStyle(int change) {
        int index = (Style.STYLES.indexOf(Configuration.instance().client.style) + change) % Style.AMOUNT;

        if (index < 0) {
            this.cycleStyle(Style.AMOUNT + index);
        } else {
            Configuration.instance().client.style = Style.STYLES.get(index);
            this.refresh();
        }
    }

    public ScalableWidget centeredButton(int y, int buttonWidth, Text text, PressCallback<ScalableWidget> action) {
        return ScalableWidgets.button().x((this.width - buttonWidth) / 2).y(y).width(buttonWidth).height(20).text(text).primaryAction(action);
    }

    public ScalableWidget squareButton(int x, int y, Text text, PressCallback<ScalableWidget> action) {
        return ScalableWidgets.button().x(x - 10).y(y - 10).width(20).height(20).text(text).primaryAction(action);
    }

    public ScalableWidget resetButton(PressCallback<ScalableWidget> action) {
        return ScalableWidgets.button().x(this.width - this.width / 24 - 112).y(this.height - this.height / 16 - 20).width(112).height(20).text(Translations.menuButtonReset).primaryAction(action);
    }

    public ScalableWidget optionButton(int row, Text text, PressCallback<ScalableWidget> primaryAction, PressCallback<ScalableWidget> secondaryAction) {
        return ScalableWidgets.button()
            .x(this.optionX())
            .y(this.optionY(row))
            .width(100)
            .height(20)
            .text(text)
            .primaryAction(primaryAction)
            .secondaryAction(secondaryAction);
    }

    public RGBASlider colorSlider(double currentValue, Text text, int id) {
        return new RGBASlider(this.optionX(), this.optionY(id), 100, 20, text, currentValue, id);
    }

    public ScalableWidget[] addPointButtons(int rows, int points, PressCallback<ScalableWidget> action) {
        ScalableWidget[] buttons = new ScalableWidget[rows];
        int start = (this.height - (rows - 1) * this.height / 16) / 2;

        for (int row = 0; row < rows; row++) {
            buttons[row] = squareButton((this.width + 162) / 2, start + row * this.height / 16 + 4, new LiteralText("+"), action);
            buttons[row].active = points > 0;
        }

        return buttons;
    }

    public ScalableWidget[] removePointButtons(int rows, PressCallback<ScalableWidget> action) {
        ScalableWidget[] buttons = new ScalableWidget[rows];
        int start = (this.height - (rows - 1) * this.height / 16) / 2;

        for (int row = 0; row < rows; row++) {
            buttons[row] = this.squareButton((this.width + 162) / 2 - 20, start + row * this.height / 16 + 4, new LiteralText("-"), action);
        }

        return buttons;
    }

    public int optionX() {
        return Math.round(this.width * (1 - 1 / 24F)) - 100;
    }

    public int optionY(int row) {
        return this.height / 16 + Math.max(this.height / 16 * row, 30 * row);
    }

    protected PressCallback<ScalableWidget> bindSlotAction() {
        return (ScalableWidget button) -> ClientPlayNetworking.send(Packets.serverBindSlot, new ExtendedPacketBuffer(this.storage).writeInt(this.slot));
    }

    protected PressCallback<ScalableWidget> cycleStyleAction(int change) {
        return (ScalableWidget button) -> this.cycleStyle(change);
    }

    protected PressCallback<ScalableWidget> resetAction(Category category) {
        return (ScalableWidget button) -> ClientPlayNetworking.send(Packets.serverReset, new ExtendedPacketBuffer(this.storage).writeIdentifier(category.identifier()));
    }
}
