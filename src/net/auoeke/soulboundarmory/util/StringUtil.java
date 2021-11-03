package net.auoeke.soulboundarmory.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String macroCaseToCamelCase(String string) {
         StringBuilder builder = new StringBuilder();
         char[] chars = string.toLowerCase().toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char character = chars[i];

            if (character == '_') {
                character = (char) (chars[++i] - 32);
            }

            builder.append(character);
        }

        return builder.toString();
    }

    public static boolean contains(String string, String regex) {
        return Pattern.compile(regex).matcher(string).find();
    }

    public static Matcher match(String string, String regex) {
        return Pattern.compile(regex).matcher(string);
    }

    public static int lastIndex(String string, int end, int character) {
        return string.substring(0, end).lastIndexOf(character);
    }
}
