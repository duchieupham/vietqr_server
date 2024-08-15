package com.vietqr.org.util;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatterUtil {
    public static <T> String formatNumber(T number) {
        String stringNumber = number.toString();
        StringBuilder result = new StringBuilder();
        int length = stringNumber.length();
        int commaPosition = length % 3;
        if (commaPosition > 0) {
            result.append(stringNumber, 0, commaPosition);
            if (length > 3) {
                result.append(",");
            }
        }
        for (int i = commaPosition; i < length; i += 3) {
            result.append(stringNumber, i, i + 3);
            if (i + 3 < length) {
                result.append(",");
            }
        }
        return result.toString();
    }
}
