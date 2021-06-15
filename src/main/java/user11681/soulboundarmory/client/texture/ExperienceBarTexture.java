package user11681.soulboundarmory.client.texture;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import user11681.soulboundarmory.client.gui.ExperienceBarOverlay;
import user11681.soulboundarmory.util.Resources;

@OnlyIn(Dist.CLIENT)
public class ExperienceBarTexture extends DynamicTexture {
    public ExperienceBarTexture() {
        super(256, 256, true);

        Minecraft.getInstance().getTextureManager().register("soulboundarmory/gui/experience_bar", this);
    }

    @Override
    public void load(IResourceManager manager) {
        super.load(manager);

        BufferedImage image = Resources.readTexture(AbstractGui.GUI_ICONS_LOCATION);
        WritableRaster raster = image.getRaster();
        List<ExperienceBarOverlay.Style> styles = ExperienceBarOverlay.Style.styles;
        int amount = ExperienceBarOverlay.Style.count;
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

        NativeImage nativeImage = this.getPixels();

        for (int bar = 0; bar < amount; bar++) {
            int y = styles.get(bar).v;

            for (int state = 0; state < 2; state++) {
                y += state * 5;

                for (int row = 0; row < scaledHeight; row++) {
                    int v = (int) (y * scale) + row;

                    for (int u = 0; u < scaledWidth; u++) {
                        int[] pixel = raster.getPixel(u, v, (int[]) null);
                        int color = (int) (multipliers[bar] * Math.round(Math.sqrt(0.299F * pixel[0] * pixel[0] + 0.587F * pixel[1] * pixel[1] + 0.114F * pixel[2] * pixel[2])));

                        nativeImage.setPixelRGBA(u, v, new Color(color, color, color).getRGB());
                    }
                }
            }
        }

        var bp = true;
    }
}
