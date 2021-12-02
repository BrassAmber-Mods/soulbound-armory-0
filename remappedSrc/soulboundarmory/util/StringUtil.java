package soulboundarmory.util;

import ;
import C;
import I;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String macroCaseToCamelCase(String string) {
        StringBuilder builder = new StringBuilder();
        [C chars = string.toLowerCase().toCharArray();

        for (I i = 0; i < chars.length; i++) {
            C character = chars[i];

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
