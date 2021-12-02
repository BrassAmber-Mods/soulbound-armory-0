package soulboundarmory.client.gui;

import cell.client.gui.widget.Slider;
import net.minecraft.text.Text;
import soulboundarmory.config.Configuration;
import soulboundarmory.text.Translation;

public class RGBASlider extends Slider {
    protected static final Configuration.Client.Colors colors = Configuration.instance().client.colors;

    public final int id;

    protected final Text text;

    protected int componentValue;

    public RGBASlider(int id, Text text) {
        this.min(0).max(255);

        this.text = text;
        this.id = id;

        this.value(colors.get(id));
        this.updateMessage();
        this.updateMessage();
    }

    @Override
    protected void applyValue() {
        this.setMessage(new Translation("%s: %s", this.text, this.componentValue));
    }

    @Override
    protected void updateMessage() {
        this.componentValue = (int) (0xFF * this.value);
        colors.set(this.id, this.componentValue);
    }
}
