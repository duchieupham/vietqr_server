package com.vietqr.org.util;

public class EnvironmentUtil {
    private static final String BANK_URL = "https://api-private.mbbank.com.vn/";

    private static final String USER_BANK_ACCESS = "LP6GX1nahlNKYXPLZZ8xOvmmarO2JAMJ";
    private static final String PASSWORD_BANK_ACCESS = "LB9xgPoukVu6hOrA";

    public static String getBankUrl() {
        return BANK_URL;
    }

    public static String getUserBankAccess() {
        return USER_BANK_ACCESS;
    }

    public static String getPasswordBankAccess() {
        return PASSWORD_BANK_ACCESS;
    }

}
