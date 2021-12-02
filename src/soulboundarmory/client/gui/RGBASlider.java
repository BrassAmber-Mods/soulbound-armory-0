package soulboundarmory.client.gui;

import cell.client.gui.widget.Slider;
import soulboundarmory.config.Configuration;
import soulboundarmory.text.Translation;
import net.minecraft.util.text.ITextComponent;

public class RGBASlider extends Slider {
    protected static final Configuration.Client.Colors colors = Configuration.instance().client.colors;

    public final int id;

    protected final ITextComponent text;

    protected int componentValue;

    public RGBASlider(int id, ITextComponent text) {
        this.min(0).max(255);

        this.text = text;
        this.id = id;

        this.value(colors.get(id));
        this.func_230979_b_();
        this.func_230979_b_();
    }

    @Override
    protected void func_230972_a_() {
        this.setMessage(new Translation("%s: %s", this.text, this.componentValue));
    }

    @Override
    protected void func_230979_b_() {
        this.componentValue = (int) (0xFF * this.sliderValue);
        colors.set(this.id, this.componentValue);
    }
}
