package user11681.soulboundarmory.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class Resources {
    public static final IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

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
             byte[] content = new byte[input.available()];

            while (input.read(content) > -1);

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
            return resourceManager.getResource(identifier);
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
         int[][][] pixels = new int[image.getHeight()][image.getWidth()][4];
         Raster raster = image.getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = raster.getPixel(u + x, v + y, (int[]) null);
            }
        }

        return pixels;
    }

    public static int[] rgb(int color) {
         int[] rgb = new int[3];

        rgb[0] = color >> 16 & 0xFF;
        rgb[1] = color >> 8 & 0xFF;
        rgb[2] = color & 0xFF;

        return rgb;
    }
}
