package user11681.soulboundarmory.client.texture;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.cell.client.gui.CellElement;
import user11681.soulboundarmory.SoulboundArmory;
import user11681.soulboundarmory.client.gui.bar.Style;
import user11681.soulboundarmory.util.MathUtil;
import user11681.soulboundarmory.util.Resources;

@OnlyIn(Dist.CLIENT)
public class ExperienceBarTexture extends ResourceTexture {
    public static final ExperienceBarTexture instance = new ExperienceBarTexture();

    private ExperienceBarTexture() {
        super(SoulboundArmory.id("gui/experience_bar"));

        CellElement.textureManager.registerTexture(this.location, this);
    }

    @Override
    protected TextureData loadTextureData(ResourceManager resourceManager) {
        BufferedImage image = Resources.readTexture(DrawableHelper.GUI_ICONS_TEXTURE);
        WritableRaster raster = image.getRaster();
        List<Style> styles = Style.styles;
        int amount = Style.count;
        float scale = image.getWidth() / 256F;
        int scaledWidth = (int) (182 * scale);
        int scaledHeight = (int) (5 * scale);
        float[] multipliers = new float[amount];

        Arrays.fill(multipliers, Float.MAX_VALUE);

        for (int bar = 0; bar < amount; bar++) {
            int v = styles.get(bar).v;

            for (int state = 0; state < 2; state++) {
                v += state * 5;

                for (int[][] row : Resources.pixels(image, 0, (int) (v * scale), scaledWidth, scaledHeight)) {
                    for (int[] pixel : row) {
                        float multiplier = (float) (255D / Math.sqrt(0.299F * pixel[0] * pixel[0] + 0.587F * pixel[1] * pixel[1] + 0.114F * pixel[2] * pixel[2]));

                        if (multiplier < multipliers[bar]) {
                            multipliers[bar] = multiplier;
                        }
                    }
                }
            }
        }

        NativeImage nativeImage = new NativeImage(256, 256, true);

        for (int bar = 0; bar < amount; bar++) {
            int y = styles.get(bar).v;

            for (int state = 0; state < 2; state++) {
                y += state * 5;

                for (int row = 0; row < scaledHeight; row++) {
                    int v = (int) (y * scale) + row;

                    for (int u = 0; u < scaledWidth; u++) {
                        int[] pixel = raster.getPixel(u, v, (int[]) null);
                        int color = (int) (multipliers[bar] * Math.round(Math.sqrt(0.299F * pixel[0] * pixel[0] + 0.587F * pixel[1] * pixel[1] + 0.114F * pixel[2] * pixel[2])));

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
