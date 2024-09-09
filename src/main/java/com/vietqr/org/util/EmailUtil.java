package com.vietqr.org.util;

import java.util.regex.Pattern;

public class EmailUtil {
    // Email regex pattern (case insensitive)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Verifies if the given string is a valid email format.
     *
     * @param email the string to check
     * @return true if the string is a valid email, false otherwise
     */
    public static boolean isVerified(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
