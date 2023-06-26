package com.vietqr.org.util;

public class EnvironmentUtil {
    private static boolean IS_PRODUCTION = false;

    // MB Bank
    private static final String BANK_URL_UAT = "https://api-sandbox.mbbank.com.vn/";
    private static final String BANK_URL_PRODUCT = "https://api-private.mbbank.com.vn/private/";

    private static final String USER_BANK_ACCESS_UAT = "pQCpcZuXhGRkb3VRDrVNPY1nmlmL9tGe";
    private static final String PASSWORD_BANK_ACCESS_UAT = "a9TGt9vBqHRpU6q2";

    private static final String USER_BANK_MMS_ACCESS_UAT = "RKzfCQIZBosvPVSXbi4kL4LRg45njNjr";
    private static final String PASSWORD_BANK_MMS_ACCESS_UAT = "6eV24s6QAysGlo8w";

    private static final String USER_BANK_ACCESS_PROD = "LP6GX1nahlNKYXPLZZ8xOvmmarO2JAMJ";
    private static final String PASSWORD_BANK_ACCESS_PROD = "LB9xgPoukVu6hOrA";

    private static final String USER_BANK_MMS_ACCESS_PROD = "LP6GX1nahlNKYXPLZZ8xOvmmarO2JAMJ";
    private static final String PASSWORD_BANK_MMS_ACCESS_PROD = "LB9xgPoukVu6hOrA";

    private static final String SECRET_KEY_API_UAT = "32KqWUGhGum1lEAi6WsHlUha7Kk0Ck9JeMBMmnIfdkoajRrOHVgAWBcX7rIRH1LD";
    private static final String SECRET_KEY_API_PROD = "vuSMiHQ3tH2auAVHzXQiMgQQCzcdlpvq3Bb0wQRF4dBxdjojMj0LQnGUPE24bGqr";

    private static final String USERNAME_API_UAT = "MB_BLC";
    private static final String USERNAME_API_PROD = "MB_BLC";

    // SFTP VietQR
    private static final String SFTP_USER_UAT = "root";
    private static final String SFTP_PASSWORD_UAT = "4G01T1r3!Ab1";

    private static final String SFTP_USER_PROD = "root";
    private static final String SFTP_PASSWORD_PROD = "5uQ26Jwa!Ab1";

    private static final String SFTP_HOSTING_UAT = "112.78.1.220";
    private static final String SFTP_HOSTING_PROD = "112.78.1.209";

    private static final int PORT = 22;

    private static final String INPUT_FOLDER = "/usr/data/transactions/in/";
    private static final String OUTPUT_FOLDER = "/usr/data/transactions/out/";

    public static String getSecretKeyAPI() {
        return (IS_PRODUCTION == false) ? SECRET_KEY_API_UAT : SECRET_KEY_API_PROD;
    }

    public static String getUsernameAPI() {
        return (IS_PRODUCTION == false) ? USERNAME_API_UAT : USERNAME_API_PROD;
    }

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
