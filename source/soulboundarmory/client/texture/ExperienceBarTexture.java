package soulboundarmory.client.texture;

import soulboundarmory.lib.gui.CellElement;
import java.util.Arrays;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import soulboundarmory.client.gui.bar.BarStyle;
import soulboundarmory.util.Math2;
import soulboundarmory.util.Resources;
import soulboundarmory.util.Util;

public class ExperienceBarTexture extends ResourceTexture {
    public static final ExperienceBarTexture instance = new ExperienceBarTexture();

    private ExperienceBarTexture() {
        super(Util.id("gui/experience_bar"));

        CellElement.textureManager.registerTexture(this.location, this);
    }

    @Override
    protected TextureData loadTextureData(ResourceManager manager) {
        var image = Resources.readTexture(DrawableHelper.GUI_ICONS_TEXTURE);
        var raster = image.getRaster();
        var styles = BarStyle.styles;
        var amount = BarStyle.count;
        var scale = image.getWidth() / 256F;
        var scaledWidth = (int) (182 * scale);
        var scaledHeight = (int) (5 * scale);
        var multipliers = new float[amount];

        Arrays.fill(multipliers, Float.MAX_VALUE);

        for (var bar = 0; bar < amount; bar++) {
            var v = styles.get(bar).v;

            for (var state = 0; state < 2; state++) {
                v += state * 5;

                for (var row : Resources.pixels(image, 0, (int) (v * scale), scaledWidth, scaledHeight)) {
                    for (var pixel : row) {
                        var multiplier = (float) (255D / Math.sqrt(0.299F * pixel[0] * pixel[0] + 0.587F * pixel[1] * pixel[1] + 0.114F * pixel[2] * pixel[2]));

                        if (multiplier < multipliers[bar]) {
                            multipliers[bar] = multiplier;
                        }
                    }
                }
            }
        }

        var nativeImage = new NativeImage(256, 256, true);

        for (var bar = 0; bar < amount; bar++) {
            var y = styles.get(bar).v;

            for (var state = 0; state < 2; state++) {
                y += state * 5;

                for (var row = 0; row < scaledHeight; row++) {
                    var v = (int) (y * scale) + row;

                    for (var u = 0; u < scaledWidth; u++) {
                        var pixel = raster.getPixel(u, v, (int[]) null);
                        var color = (int) (multipliers[bar] * Math.round(Math.sqrt(0.299F * pixel[0] * pixel[0] + 0.587F * pixel[1] * pixel[1] + 0.114F * pixel[2] * pixel[2])));

                        if (pixel[3] > 0) {
                            nativeImage.setColor(u, v, Math2.pack(color, color, color));
                        }
                    }
                }
            }
        }

        return new TextureData(null, nativeImage);
    }
}
