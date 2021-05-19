package user11681.soulboundarmory.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.ResourceLocation;
import user11681.usersmanual.Main;

@SuppressWarnings("ConstantConditions")
public class Resources {
    public static final ResourceManager RESOURCE_MANAGER = Minecraft.getInstance().getResourceManager();

    public static ByteArrayInputStream toInputStream(Raster raster) {
        return new ByteArrayInputStream(((DataBufferByte) raster.getDataBuffer()).getData());
    }

    public static NativeImage toNativeImage(Raster raster) {
        try {
            return NativeImage.read(toInputStream(raster));
        } catch (IOException exception) {
            Main.logger.error("An error occurred while attempting to read an image:", exception);
        }

        return null;
    }

    public static BufferedImage readTexture(ResourceLocation identifier) {
        try {
            return ImageIO.read(getInputStream(identifier));
        } catch (IOException exception) {
            Main.logger.error("An error occurred in an attempt get the image of a texture.", exception);
        }

        return null;
    }

    public static byte[] getBytes(ResourceLocation resource) {
        return getBytes(getInputStream(resource));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static byte[] getBytes(InputStream input) {
        try {
            final byte[] content = new byte[input.available()];

            while (input.read(content) > -1);

            return content;
        } catch (IOException exception) {
            throw new RuntimeException("An error occurred while attempting to read a resource to a byte array.", exception);
        }
    }

    public static InputStream getInputStream(ResourceLocation resource) {
        return getResource(resource).getInputStream();
    }

    public static Resource getResource(ResourceLocation identifier) {
        try {
            return RESOURCE_MANAGER.getResource(identifier);
        } catch (IOException exception) {
            Main.logger.error(String.format("Resource %s was not found.", identifier), exception);
        }

        return null;
    }

    public static int[][][] getPixels(ResourceLocation texture) {
        return getPixels(readTexture(texture));
    }

    public static int[][][] getPixels(BufferedImage image) {
        return getPixels(image, 0, 0, image.getWidth(), image.getHeight());
    }

    public static int[][][] getPixels(BufferedImage image, final int u, final int v, final int width, final int height) {
        final int[][][] pixels = new int[image.getHeight()][image.getWidth()][4];
        final Raster raster = image.getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = raster.getPixel(u + x, v + y, (int[]) null);
            }
        }

        return pixels;
    }

    public static int[] getRGB(int color) {
        final int[] rgb = new int[3];

        rgb[0] = color >> 16 & 0xFF;
        rgb[1] = color >> 8 & 0xFF;
        rgb[2] = color & 0xFF;

        return rgb;
    }
}
