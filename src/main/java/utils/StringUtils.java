package utils;

public class StringUtils {

    private StringUtils() {

    }

    public static boolean isBlankOrEmpty(String target) {
        return target == null || target.isBlank() || target.isEmpty();
    }

    public static String joinNameAndValue(String delimiter, String name, String value) {
        return String.join(delimiter, name, value);
    }
}
