package soulboundarmory.client.texture;

import java.util.Arrays;
import cell.client.gui.CellElement;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.gui.bar.Style;
import soulboundarmory.util.MathUtil;
import soulboundarmory.util.Resources;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExperienceBarTexture extends SimpleTexture {
    public static final ExperienceBarTexture instance = new ExperienceBarTexture();

    private ExperienceBarTexture() {
        super(SoulboundArmory.id("gui/experience_bar"));

        CellElement.textureManager.loadTexture(this.textureLocation, this);
    }

    @Override
    protected TextureData getTextureData(IResourceManager manager) {
        var image = Resources.readTexture(AbstractGui.GUI_ICONS_LOCATION);
        var raster = image.getRaster();
        var styles = Style.styles;
        var amount = Style.count;
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
                            nativeImage.setPixelRGBA(u, v, MathUtil.pack(color, color, color));
                        }
                    }
                }
            }
        }

        return new TextureData(null, nativeImage);
    }
}
