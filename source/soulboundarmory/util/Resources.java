package soulboundarmory.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import soulboundarmory.module.gui.AbstractNode;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public class Resources {
    public static BufferedImage readTexture(Identifier identifier) {
        try {
            try (var resource = resource(identifier)) {
                return ImageIO.read(resource.getInputStream());
            }
        } catch (IOException exception) {
            throw Unsafe.throwException(exception);
        }
    }

    public static byte[] bytes(InputStream input) {
        try {
            return input.readAllBytes();
        } catch (IOException exception) {
            throw Unsafe.throwException(exception);
        }
    }

    public static Resource resource(Identifier identifier) {
        try {
            return AbstractNode.resourceManager.getResource(identifier);
        } catch (IOException exception) {
            throw Unsafe.throwException(exception);
        }
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
