package soulboundarmory.client.texture;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.List;
import ;
import F;
import I;
import cell.client.gui.CellElement;
import soulboundarmory.SoulboundArmory;
import soulboundarmory.client.gui.bar.Style;
import soulboundarmory.util.MathUtil;
import soulboundarmory.util.Resources;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExperienceBarTexture extends ResourceTexture {
    public static final ExperienceBarTexture instance = new ExperienceBarTexture();

    private ExperienceBarTexture() {
        super(SoulboundArmory.id("gui/experience_bar"));

        CellElement.textureManager.registerTexture(this.location, this);
    }

    @Override
    protected TextureData loadTextureData(ResourceManager manager) {
        BufferedImage image = Resources.readTexture(DrawableHelper.GUI_ICONS_TEXTURE);
        WritableRaster raster = image.getRaster();
        List styles = Style.styles;
        I amount = Style.count;
        F scale = image.getWidth() / 256F;
        I scaledWidth = (int) (182 * scale);
        I scaledHeight = (int) (5 * scale);
        [F multipliers = new float[amount];

        Arrays.fill(multipliers, Float.MAX_VALUE);

        for (I bar = 0; bar < amount; bar++) {
            I v = styles.get(bar).v;

            for (I state = 0; state < 2; state++) {
                v += state * 5;

                for (I row : Resources.pixels(image, 0, (int) (v * scale), scaledWidth, scaledHeight)) {
                    for (I pixel : row) {
                        F multiplier = (float) (255D / Math.sqrt(0.299F * pixel[0] * pixel[0] + 0.587F * pixel[1] * pixel[1] + 0.114F * pixel[2] * pixel[2]));

                        if (multiplier < multipliers[bar]) {
                            multipliers[bar] = multiplier;
                        }
                    }
                }
            }
        }

        NativeImage nativeImage = new NativeImage(256, 256, true);

        for (I bar = 0; bar < amount; bar++) {
            I y = styles.get(bar).v;

            for (I state = 0; state < 2; state++) {
                y += state * 5;

                for (I row = 0; row < scaledHeight; row++) {
                    I v = (int) (y * scale) + row;

                    for (I u = 0; u < scaledWidth; u++) {
                        [I pixel = raster.getPixel(u, v, (int[]) null);
                        I color = (int) (multipliers[bar] * Math.round(Math.sqrt(0.299F * pixel[0] * pixel[0] + 0.587F * pixel[1] * pixel[1] + 0.114F * pixel[2] * pixel[2])));

                        if (pixel[3] > 0) {
                            nativeImage.setPixelColor(u, v, MathUtil.pack(color, color, color));
                        }
                    }
                }
            }
        }

        return new TextureData(null, nativeImage);
    }
}
