package com.vietqr.org.util;

import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.BoxEnvironmentVarDTO;
import org.apache.log4j.Logger;

public class StringUtil {

    private static final Logger logger = Logger.getLogger(StringUtil.class);

    public static String convertToHexadecimal(long value) {
        String result = "";
        try {
            result = Long.toHexString(value);
            result = String.format("%5s", result).replace(' ', '0').toUpperCase();
        } catch (Exception e) {
            logger.error("StringUtil: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis()
            + " value: " + value);
        }
        return result;
    }

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

    public static boolean isLatinAndNumeric(String input) {
        boolean result = false;
        try {
            result = !isNullOrEmpty(input) && input.matches("[a-zA-Z0-9]*");
        } catch (Exception e) {
            logger.error("isLatinAlphabet: ERROR: " + e.toString());
        }
        return true;
    }

    public static String getValueNullChecker(String value) {
        return value != null ? value : "";
    }

    public static int getValueNullChecker(Integer value) {
        return value != null ? value : 0;
    }

    public static double getValueNullChecker(Double value) {
        return value != null ? value : 0;
    }

    public static String getValueNullChecker(String value, String expected) {
        return value != null ? value : expected;
    }

    public static int getValueNullChecker(Integer value, int expected) {
        return value != null ? value : expected;
    }

    public static double getValueNullChecker(Double value, double expected) {
        return value != null ? value : expected;
    }

    public static int getTotalPage(int totalElement, int size) {
        int result = 0;
        result = totalElement % size == 0 ?
                totalElement / size : totalElement / size + 1;
        return result;
    }

    public static String getValueNotNull(String value) {
        return value != null ? value : "";
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

    public static boolean isValidRegular(String value, String regex) {
        return value != null && value.matches(regex);
    }

    public static String removeFormatNumber(String debitAmount) {
        String result = "";
        try {
            result = debitAmount.replaceAll(".", "");
            result = debitAmount.replaceAll(",", "");
        } catch (Exception e) {
            result = debitAmount;
        }
        return result;
    }

    public static String formatNumberAsString(String amount) {
        String result = amount;
        try {
            if (StringUtil.containsOnlyDigits(amount)) {
                NumberFormat nf = NumberFormat.getInstance(Locale.US);
                Long numberAmount = Long.parseLong(amount);
                result = nf.format(numberAmount);
            }
        } catch (Exception e) {
            result = amount;
        }
        return result;
    }

    private static String getQuarter(int month) {
        String result = "";
        switch (month) {
            case 1:
                result = "01";
                break;
            case 2:
                result = "04";
                break;
            case 3:
                result = "07";
                break;
            case 4:
                result = "10";
                break;
            default:
                if (month > 4) result = "01";
                else result = "10";
                break;
        }
        return result;
    }

    public static List<String> getStartQuarter(int month, String yearAsString) {
        List<String> result = new ArrayList<>();
        int quarter = (month / 3) + 1;
        int pre = month % 3;
        int year = Integer.parseInt(yearAsString);
        try {
            if (year <= 23 && month < 12) {
                result.add("12");
            } else if (year == 23 && month == 12) {
                result.add("12");
                result.add("01");
            } else {
                switch (pre) {
                    case 0:
                        result.add(getQuarter(quarter));
                        if (year >= 24) {
                            result.add(getQuarter(quarter - 1));
                        }
                        break;
                    case 1:
                        result.add(getQuarter(quarter));
                        if (year >= 24) {
                            result.add(getQuarter(quarter + 1));
                        }
                        break;
                    case 2:
                        result.add(getQuarter(quarter));
                        break;
                    default:
                        result.add("");
                        break;
                }
            }
        } catch (Exception e) {
            result = new ArrayList<>();
        }
        return result;
    }

    public static String getMessageBox(String value) {
        String result = "%s";
        try {
            ObjectMapper mapper = new ObjectMapper();
            BoxEnvironmentVarDTO dto = mapper.readValue(value, BoxEnvironmentVarDTO.class);
            result = dto.getMessage1() + " %s " + EnvironmentUtil.getVietQrPaymentSuccessQrVoice() + dto.getMessage2();
        } catch (Exception e) {
            result = "%s";
        }
        return result;
    }

    public static String removeDiacritics(String input) {
        // Normalize the input string to decompose diacritics
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // Use a regular expression to remove all diacritics
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    public static String removeMarkString(String value) {
        return value != null ? value.replaceAll("\"", "") : "";
    }

    public static boolean getValueNullChecker(Boolean value) {
        return value != null ? value : false;
    }

    public static long getValueNullChecker(Long value) {
        return value != null ? value : 0;
    }
}
