package com.vietqr.org.util;

public class FormatUtil {

    public static boolean isNumber(String text) {
        boolean result = false;
        try {
            Long.parseLong(text);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
