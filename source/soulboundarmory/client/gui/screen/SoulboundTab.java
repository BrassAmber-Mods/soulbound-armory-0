package soulboundarmory.client.gui.screen;

import cell.client.gui.widget.Widget;
import cell.client.gui.widget.scalable.ScalableWidget;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.component.statistics.Category;
import soulboundarmory.network.ExtendedPacketBuffer;
import soulboundarmory.network.Packets;

public abstract class SoulboundTab extends Widget<SoulboundTab> {
    protected static final NumberFormat format = DecimalFormat.getInstance();

    public final Text title;
    public int index;
    public Widget<?> button;

    public SoulboundTab(Text title) {
        this.title = title;
    }

    public int top(int rows) {
        return this.top(this.height() / 16, rows);
    }

    public int top(int separation, int rows) {
        return this.middleY() - (rows - 1) * separation / 2;
    }

    public int height(int rows, int row) {
        return this.top(rows) + row * this.height() / 16;
    }

    public int height(int separation, int rows, int row) {
        return this.top(rows, separation) + row * separation;
    }

    public Widget<?> squareButton(int x, int y, String text, Runnable action) {
        return new ScalableWidget<>().button()
            .x(x - 10)
            .y(y - 10)
            .width(20)
            .height(20)
            .text(Text.of(text))
            .primaryAction(action);
    }

    public Widget<?> resetButton(Category category) {
        return new ScalableWidget<>().button()
            .x((int) MathHelper.lerp(23F / 24, 0, this.width()) - 112)
            .y((int) MathHelper.lerp(15F / 16, 0, this.height()) - 20)
            .width(112)
            .height(20)
            .text(Translations.guiButtonReset)
            .primaryAction(this.resetAction(category));
    }

    @Override
    public SoulboundTab parent(Widget<?> parent) {
        if (parent != null) {
            this.width(parent.width()).height(parent.height());
        }

        return super.parent(parent);
    }

    protected SoulboundScreen parent() {
        return (SoulboundScreen) super.parent.get();
    }

    protected Runnable resetAction(Category category) {
        return () -> Packets.serverReset.send(new ExtendedPacketBuffer(this.parent().item).writeIdentifier(category.id()));
    }

    protected void displayPoints(int points) {
        drawCenteredText(this.matrixes, textRenderer, this.pointText(points), Math.round(this.width() / 2F), 4, 0xFFFFFF);
    }

    protected Text pointText(int points) {
        return switch (points) {
            case 0 -> LiteralText.EMPTY;
            case 1 -> Translations.guiUnspentPoint;
            default -> Translations.guiUnspentPoints.format(points);
        };
    }
}
