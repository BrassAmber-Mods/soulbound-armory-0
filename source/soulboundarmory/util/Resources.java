package soulboundarmory.util;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import soulboundarmory.module.gui.Node;

public class Resources {
    public static BufferedImage readTexture(Identifier identifier) {
        try (var resource = resource(identifier)) {
            return ImageIO.read(resource.getInputStream());
        }
    }

    public static Resource resource(Identifier identifier) {
        return Node.resourceManager.getResource(identifier);
    }

    public static int[][][] pixels(BufferedImage image, int u, int v, int width, int height) {
        var pixels = new int[image.getHeight()][image.getWidth()][4];
        var raster = image.getData();

        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                pixels[y][x] = raster.getPixel(u + x, v + y, (int[]) null);
            }
        }

        return pixels;
    }
}
