package com.vietqr.org.util;

public class EnvironmentUtil {
    private static boolean IS_PRODUCTION = true;
    private static final String BANK_URL_UAT = "https://api-sandbox.mbbank.com.vn/";
    private static final String BANK_URL_PRODUCT = "https://api-private.mbbank.com.vn/private/";

    private static final String USER_BANK_ACCESS_UAT = "pQCpcZuXhGRkb3VRDrVNPY1nmlmL9tGe";
    private static final String PASSWORD_BANK_ACCESS_UAT = "a9TGt9vBqHRpU6q2";

    private static final String USER_BANK_MMS_ACCESS_UAT = "RKzfCQIZBosvPVSXbi4kL4LRg45njNjr";
    private static final String PASSWORD_BANK_MMS_ACCESS_UAT = "6eV24s6QAysGlo8w";

    private static final String USER_BANK_ACCESS_PROD = "LP6GX1nahlNKYXPLZZ8xOvmmarO2JAMJ";
    private static final String PASSWORD_BANK_ACCESS_PROD = "LB9xgPoukVu6hOrA";

    private static final String USER_BANK_MMS_ACCESS_PROD = "";
    private static final String PASSWORD_BANK_MMS_ACCESS_PROD = "";

    public static String getBankUrl() {
        return (IS_PRODUCTION == false) ? BANK_URL_UAT : BANK_URL_PRODUCT;
    }

    public static String getUserBankAccess() {
        return (IS_PRODUCTION == false) ? USER_BANK_ACCESS_UAT : USER_BANK_ACCESS_PROD;
    }

    public static String getUserBankMMSAccess() {
        return (IS_PRODUCTION == false) ? USER_BANK_MMS_ACCESS_UAT : USER_BANK_MMS_ACCESS_PROD;
    }

    public static String getPasswordBankAccess() {
        return (IS_PRODUCTION == false) ? PASSWORD_BANK_ACCESS_UAT : PASSWORD_BANK_ACCESS_PROD;
    }

    public static String getPasswordBankMMSAccess() {
        return (IS_PRODUCTION == false) ? PASSWORD_BANK_MMS_ACCESS_UAT : PASSWORD_BANK_MMS_ACCESS_PROD;
    }

}
