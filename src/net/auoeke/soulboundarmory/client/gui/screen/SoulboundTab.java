package net.auoeke.soulboundarmory.client.gui.screen;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import net.auoeke.soulboundarmory.capability.statistics.Category;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.auoeke.cell.client.gui.widget.callback.PressCallback;
import net.auoeke.cell.client.gui.widget.scalable.ScalableWidget;
import net.auoeke.soulboundarmory.SoulboundArmoryClient;
import net.auoeke.soulboundarmory.client.i18n.Translations;
import net.auoeke.soulboundarmory.network.ExtendedPacketBuffer;
import net.auoeke.soulboundarmory.registry.Packets;

@OnlyIn(Dist.CLIENT)
public abstract class SoulboundTab extends ScreenTab {
    protected static final NumberFormat format = DecimalFormat.getInstance();

    protected SoulboundScreen parent;

    public SoulboundTab(Text title) {
        super(title);
    }

    public void open(int width, int height) {
        this.init(this.client, width, height);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (SoulboundArmoryClient.guiKeyBinding.matchesKey(keyCode, scanCode)) {
            this.onClose();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public ScalableWidget centeredButton(int y, int buttonWidth, Text text, PressCallback<ScalableWidget> action) {
        return new ScalableWidget().button()
            .centerX()
            .x(this.width)
            .y(y)
            .width(buttonWidth)
            .height(20)
            .text(text)
            .primaryAction(action);
    }

    public ScalableWidget squareButton(int x, int y, Text text, PressCallback<ScalableWidget> action) {
        return new ScalableWidget().button()
            .x(x - 10)
            .y(y - 10)
            .width(20)
            .height(20)
            .text(text)
            .primaryAction(action);
    }

    public ScalableWidget resetButton(PressCallback<ScalableWidget> action) {
        return new ScalableWidget().button()
            .x(this.width - this.width / 24 - 112)
            .y(this.height - this.height / 16 - 20)
            .width(112)
            .height(20)
            .text(Translations.menuButtonReset)
            .primaryAction(action);
    }

    public ScalableWidget[] addPointButtons(int rows, int points, PressCallback<ScalableWidget> action) {
        ScalableWidget[] buttons = new ScalableWidget[rows];
        int start = (this.height - (rows - 1) * this.height / 16) / 2;

        for (int row = 0; row < rows; row++) {
            buttons[row] = this.squareButton((this.width + 162) / 2, start + row * this.height / 16 + 4, new LiteralText("+"), action);
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

    protected PressCallback<ScalableWidget> resetAction(Category category) {
        return button -> Packets.serverReset.send(new ExtendedPacketBuffer(this.parent.storage).writeIdentifier(category.id()));
    }
}
