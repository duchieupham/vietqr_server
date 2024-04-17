package com.vietqr.org.util;

import java.util.List;

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

    public static boolean isListNullOrEmpty(List<?> list) {
        try {
            return list == null || list.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
