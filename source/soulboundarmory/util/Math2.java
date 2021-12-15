package soulboundarmory.util;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;

public class Math2 {
    public static int signum(double n) {
        return (int) Math.signum(n);
    }

    public static int signum(double x, double y, double z) {
        return signum(x) * signum(y) * signum(z);
    }

    public static double square(double value) {
        return value * value;
    }

    public static float square(float value) {
        return value * value;
    }

    public static double zenith(LivingEntity entity) {
        return square(entity.getVelocity().y) / 2 / entity.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
    }

    public static int ceil(double value) {
        var floor = (int) value;

        return value == floor ? floor : floor + 1;
    }

    public static double log(double base, double power) {
        return Math.log(power) / Math.log(base);
    }

    public static int pack(int r, int g, int b, int a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    public static int pack(int r, int g, int b) {
        return pack(r, g, b, 255);
    }

    public static int red(int color) {
        return color >> 16 & 0xFF;
    }

    public static int green(int color) {
        return color >> 8 & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }
}
