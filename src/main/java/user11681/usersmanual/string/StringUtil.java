package user11681.usersmanual.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String macroCaseToCamelCase(final String string) {
        final StringBuilder builder = new StringBuilder();
        final char[] chars = string.toLowerCase().toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char character = chars[i];

            if (character == '_') {
                character = (char) (chars[++i] - 32);
            }

            builder.append(character);
        }

        return builder.toString();
    }

    public static boolean contains(final String string, final String regex) {
        return Pattern.compile(regex).matcher(string).find();
    }

    public static Matcher match(final String string, final String regex) {
        return Pattern.compile(regex).matcher(string);
    }
}
