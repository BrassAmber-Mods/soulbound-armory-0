package soulboundarmory.client.gui.screen;

import cell.client.gui.widget.callback.PressCallback;
import cell.client.gui.widget.scalable.ScalableWidget;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import soulboundarmory.SoulboundArmoryClient;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

public abstract class SoulboundTab extends ScreenTab {
    protected static final NumberFormat format = DecimalFormat.getInstance();

    protected SoulboundScreen parent;

    public SoulboundTab(Text title) {
        super(title);
    }

    public void open(int width, int height) {
        this.init(minecraft, width, height);
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
        var buttons = new ScalableWidget[rows];
        var start = (this.height - (rows - 1) * this.height / 16) / 2;

        for (var row = 0; row < rows; row++) {
            buttons[row] = this.squareButton((this.width + 162) / 2, start + row * this.height / 16 + 4, Text.of("+"), action);
            buttons[row].active = points > 0;
        }

        return buttons;
    }

    public ScalableWidget[] removePointButtons(int rows, PressCallback<ScalableWidget> action) {
        var buttons = new ScalableWidget[rows];
        var start = (this.height - (rows - 1) * this.height / 16) / 2;

        for (var row = 0; row < rows; row++) {
            buttons[row] = this.squareButton((this.width + 162) / 2 - 20, start + row * this.height / 16 + 4, Text.of("-"), action);
        }

        return buttons;
    }

    protected PressCallback<ScalableWidget> resetAction(Category category) {
        return button -> Packets.serverReset.send(new ExtendedPacketBuffer(this.parent.storage).writeIdentifier(category.id()));
    }
}
