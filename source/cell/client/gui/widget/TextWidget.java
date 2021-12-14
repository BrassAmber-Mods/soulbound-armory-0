package cell.client.gui.widget;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.text.Text;

public class TextWidget extends Widget<TextWidget> {
    public List<Text> text = new ObjectArrayList<>();
    public boolean shadow;

    @Override
    public TextWidget text(Text text) {
        this.text.add(text);

        return this;
    }

    public TextWidget text(Iterable<Text> text) {
        text.forEach(this::text);

        return this;
    }

    public TextWidget shadow(boolean shadow) {
        this.shadow = shadow;

        return this;
    }

    @Override
    public int width() {
        return this.text.stream().map(textDrawer::getWidth).max(Comparator.naturalOrder()).orElse(0);
    }

    @Override
    public int height() {
        return fontHeight() * this.text.size();
    }

    @Override
    protected void renderWidget() {
        for (var index = 0; index < this.text.size(); index++) {
            if (this.shadow) {
                textDrawer.drawWithShadow(this.matrixes, this.text.get(index), this.x(), this.y() + fontHeight() * index, this.active() ? 0xFFFFFFFF : 0xA0FFFFFF);
            } else {
                textDrawer.draw(this.matrixes, this.text.get(index), this.x(), this.y() + fontHeight() * index, this.active() ? 0xFFFFFFFF : 0xA0FFFFFF);
            }
        }
    }
}
