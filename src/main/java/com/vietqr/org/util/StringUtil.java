package com.vietqr.org.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class StringUtil {

    private static final Logger logger = Logger.getLogger(StringUtil.class);

    public static List<String> findHashtags(String input) {
        List<String> hashtags = new ArrayList<>();
        try {
            if (input != null && !input.trim().isEmpty()) {
                Pattern pattern = Pattern.compile("#[\\p{L}\\p{N}_-]+");
                Matcher matcher = pattern.matcher(input);
                while (matcher.find()) {
                    String hashtag = matcher.group();
                    hashtags.add(hashtag);
                }
            }

        } catch (Exception e) {
            logger.error("findHashtags: ERROR: " + e.toString());
        }
        return hashtags;
    }

    public static boolean containsOnlyDigits(String amount) {
        return amount.matches("\\d+");
    }

    public static boolean isEmptyOrEqualsZero(String input) {
        return input == null || input.trim().isEmpty() || input.trim().equals("0");
    }

    public static String hiddenString(String input, int digitsToShow) {
        String result = "";
        try {
            StringBuilder hiddenString = new StringBuilder();

            for (int i = 0; i < input.length() - digitsToShow; i++) {
                hiddenString.append('*');
            }

            hiddenString.append(input.substring(input.length() - digitsToShow));
            result = hiddenString.toString();
        } catch (Exception e) {
            logger.error("hiddenString: ERROR: " + e.toString());
        }
        return result;
    }

    public static String formatBankAccount(String bankAccount) {
        String result = bankAccount;
        try {
            String formattedBankAccount = bankAccount.replaceAll("\\s+", "");
            if (formattedBankAccount.length() <= 5) {
                result = formattedBankAccount;
            } else {
                int hiddenLength = formattedBankAccount.length() - 5;
                StringBuilder hiddenString = new StringBuilder();
                for (int i = 0; i < hiddenLength; i++) {
                    hiddenString.append('x');
                }
                result = formattedBankAccount.substring(0, 2) + hiddenString.toString() +
                        formattedBankAccount.substring(formattedBankAccount.length() - 3);
            }
        } catch (Exception e) {
            logger.error("formatBankAccount: ERROR: " + e.toString());
            System.out.println("formatBankAccount: ERROR: " + e.toString());
        }

        return result;
    }

    public static String formatPhoneNumber(String phoneNumber) {
        String result = phoneNumber;
        try {
            // Xóa tất cả các khoảng trắng
            String formattedNumber = phoneNumber.replaceAll("\\s+", "");

            // Kiểm tra nếu số điện thoại bắt đầu bằng "+84"
            if (formattedNumber.startsWith("+84")) {
                // Thay thế "+84" bằng "0"
                formattedNumber = formattedNumber.replace("+84", "0");
            }
            result = formattedNumber;
        } catch (Exception e) {
            logger.error("formatPhoneNumber: ERROR: " + e.toString());
            System.out.println("formatPhoneNumber: ERROR: " + e.toString());
        }

        return result;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
