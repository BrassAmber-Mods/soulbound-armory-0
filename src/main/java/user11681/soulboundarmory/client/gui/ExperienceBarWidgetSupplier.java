package user11681.soulboundarmory.client.gui;

Translationimport net.fabricmc.api.Environment;
import user11681.soulboundarmory.client.texture.ExperienceBarTexture;
import user11681.spun.client.gui.widget.scalable.ScalableWidget;
import user11681.spun.client.gui.widget.scalable.ScalableWidgetSupplier;

@OnlyIn(Dist.CLIENT)
public interface ExperienceBarWidgetSupplier extends ScalableWidgetSupplier {
    ExperienceBarWidgetSupplier EXPERIENCE_BAR = () -> {
        final ScalableWidget bar = new ScalableWidget(new ExperienceBarTexture());
    };
}
