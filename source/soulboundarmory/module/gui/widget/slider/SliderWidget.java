package soulboundarmory.module.gui.widget.slider;

import java.text.NumberFormat;
import net.minecraft.text.Text;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.module.gui.widget.ScalableWidget;
import soulboundarmory.module.gui.widget.TextWidget;

public class SliderWidget extends ScalableWidget<SliderWidget> {
    protected static final NumberFormat floatFormat = NumberFormat.getNumberInstance();

    public final Slider slider = this.add(new Slider().slide(slider -> this.updateMessage()));
    public TextWidget text = this.add(new TextWidget().x(.5).y(.5).center());
    public Text label = Translations.empty;

    public SliderWidget() {
        this.slider().height(20).updateMessage();
    }

    @Override
    public SliderWidget text(Text label) {
        this.label = label;
        this.updateMessage();

        return this;
    }

    public SliderWidget min(double min) {
        this.slider.min(min);

        return this;
    }

    public SliderWidget max(double max) {
        this.slider.max(max);

        return this;
    }

    public SliderWidget discrete(boolean discrete) {
        this.slider.discrete(discrete);

        return this;
    }

    public SliderWidget discrete() {
        this.slider.discrete();

        return this;
    }

    public SliderWidget value(double value) {
        this.slider.value(value);

        return this;
    }

    public SliderWidget onSlide(SlideCallback callback) {
        this.slider.slide(callback);

        return this;
    }

    public double value() {
        return this.slider.value;
    }

    protected void updateMessage() {
        var formattedValue = this.slider.discrete || Math.abs(this.value()) >= 100 ? (long) this.value() : floatFormat.format(this.value());
        this.text.overwrite(Text.of(this.label == Translations.empty ? String.valueOf(formattedValue) : "%s: %s".formatted(this.label, formattedValue)));
    }
}
