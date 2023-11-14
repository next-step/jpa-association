package utils;

import java.util.regex.Pattern;

public final class StringUtils {

    /**
     * Object가 문자열인 경우 콜론과 함께 반환합니다.
     * 예) 'apple', '1'
     */
    public static String parseChar(Object value) {
        if(value == null || isEmpty(value.toString())) {
            return null;
        }

        final String REGEX = "[-+]?\\d*\\.?\\d+";
        Pattern pattern = Pattern.compile(REGEX);

        String v = value.toString();
        String type = value.getClass().getSimpleName();

        if (!pattern.matcher(v).find()) {
            v = String.format("'%s'", value);
        }

        if (type.equals("String") || type.equals("char") || type.equals("Character")) {
            v = String.format("'%s'", value);
        }

        return v;
    }

    public static String camelToSnake(String input) {
        if(isEmpty(input)) {
            return null;
        }

        return input.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }

    public static boolean isEmpty(String input) {
        if (input == null || " ".equals(input) || input.length() == 0) {
            return true;
        }

        return false;
    }
}
