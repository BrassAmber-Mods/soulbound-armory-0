package soulboundarmory.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import cell.client.gui.CellElement;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class Resources {
    public static ByteArrayInputStream inputStream(Raster raster) {
        return new ByteArrayInputStream(((DataBufferByte) raster.getDataBuffer()).getData());
    }

    public static NativeImage nativeImage(Raster raster) {
        try {
            return NativeImage.read(inputStream(raster));
        } catch (IOException exception) {
            throw Unsafe.throwException(exception);
        }
    }

    public static BufferedImage readTexture(ResourceLocation identifier) {
        try {
            return ImageIO.read(inputStream(identifier));
        } catch (IOException exception) {
            throw Unsafe.throwException(exception);
        }
    }

    public static byte[] bytes(ResourceLocation resource) {
        return bytes(inputStream(resource));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static byte[] bytes(InputStream input) {
        try {
            var content = new byte[input.available()];

            while (input.read(content) > -1) {}

            return content;
        } catch (IOException exception) {
            throw Unsafe.throwException(exception);
        }
    }

    public static InputStream inputStream(ResourceLocation resource) {
        return resource(resource).getInputStream();
    }

    public static IResource resource(ResourceLocation identifier) {
        try {
            return CellElement.resourceManager.getResource(identifier);
        } catch (IOException exception) {
            throw Unsafe.throwException(exception);
        }
    }

    public static int[][][] pixels(ResourceLocation texture) {
        return pixels(readTexture(texture));
    }

    public static int[][][] pixels(BufferedImage image) {
        return pixels(image, 0, 0, image.getWidth(), image.getHeight());
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

    public static int[] rgb(int color) {
        return new int[]{
            color >> 16 & 0xFF,
            color >> 8 & 0xFF,
            color & 0xFF
        };
    }
}
