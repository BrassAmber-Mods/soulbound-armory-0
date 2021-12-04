package soulboundarmory.util;

import java.util.Random;

public class Math2 {
    public static double signum(double first, double... others) {
        var sign = Math.signum(first);

        for (var number : others) {
            sign *= Math.signum(number);
        }

        return sign;
    }

    public static int signum(int n) {
        return (int) Math.signum(n);
    }

    public static int min(int... values) {
        var min = values[0];

        for (var value : values) {
            if (value < min) {
                min = value;
            }
        }

        return min;
    }

    public static double min(double... values) {
        var min = values[0];

        for (var value : values) {
            if (value < min) {
                min = value;
            }
        }

        return min;
    }

    public static int max(int... values) {
        var max = values[0];

        for (var value : values) {
            if (value > max) {
                max = value;
            }
        }

        return max;
    }

    public static double max(double... values) {
        var max = values[0];

        for (var value : values) {
            if (value > max) {
                max = value;
            }
        }

        return max;
    }

    public static double hypot(double... values) {
        return Math.sqrt(sqSum(values));
    }

    public static int sum(int... values) {
        var sum = 0;

        for (var value : values) {
            sum += value;
        }

        return sum;
    }

    public static double sum(double... values) {
        double sum = 0;

        for (var value : values) {
            sum += value;
        }

        return sum;
    }

    public static double sqSum(double... values) {
        double sum = 0;

        for (var value : values) {
            sum += value * value;
        }

        return sum;
    }

    public static int ceil(double value) {
        var floor = (int) value;

        return value == floor ? floor : floor + 1;
    }

    public static int ceil(float value) {
        var floor = (int) value;

        return value == floor ? floor : floor + 1;
    }

    public static int roundRandomly(double value, Random random) {
        return random.nextDouble() < 0.5 ? (int) value : ceil(value);
    }

    public static double log(double base, double power) {
        return Math.log(power) / Math.log(base);
    }

    public static int nextLog(double base, double power) {
        return ceil(log(base, power));
    }

    public static int pow(int base, int exponent) {
        return (int) Math.pow(base, exponent);
    }

    public static int pack(int r, int g, int b, int a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    public static int pack(int r, int g, int b) {
        return pack(r, g, b, 255);
    }
}
