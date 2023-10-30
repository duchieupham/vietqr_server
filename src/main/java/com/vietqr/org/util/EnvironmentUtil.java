package com.vietqr.org.util;

public class EnvironmentUtil {
    private static boolean IS_PRODUCTION = false;

    // QR LINK
    private static final String QR_LINK_UAT = "https://vietqr.vn/test/qr-generated?token=";
    private static final String QR_LINK_PROD = "https://vietqr.vn/qr-generated?token=";

    // DINO SOFT - VOICE
    private static final String VOICE_REQUEST_URL = "http://103.141.140.202:8009/tts/v1/speak";
    private static final String APP_ID_VOICE = "4a5vyvn37z4C5MNGYGsKw3dNo3Vdw4PG";
    private static final String VOICE_TYPE = "mp3";
    private static final String VOICE_CODE = "1";
    private static final String SPEED_RATE = "1.1";
    private static final int BIT_RATE = 24;

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

    private static final int SFTP_PORT = 22;

    private static final String SFTP_INPUT_FOLDER = "/usr/data/transactions/in/";
    private static final String SFTP_OUTPUT_FOLDER = "/usr/data/transactions/out/";

    private static final String VNPT_EPAY_PRIVATE_KEY_URL_UAT = "/opt/keyRSA/private_key.pem";
    private static final String VNPT_EPAY_PRIVATE_KEY_URL_PROD = "/opt/keyRSAProd/private_key.pem";

    private static final String VNPT_EPAY_PUBLIC_KEY_URL_UAT = "/opt/keyRSA/public_key.pem";
    private static final String VNPT_EPAY_PUBLIC_KEY_URL_PROD = "/opt/keyRSAProd/public_key.pem";

    // private static final String VNPT_EPAY_WEB_SERVICE_URL_UAT =
    // "http://itopup-test.megapay.net.vn:8082/CDV_Partner_Services/services/Interfaces?wsdl";
    private static final String VNPT_EPAY_WEB_SERVICE_URL_UAT = "http://naptien.thanhtoan247.net.vn:8082/CDV_Partner_Services_V1.0/services/Interfaces?wsdl";
    private static final String VNPT_EPAY_WEB_SERVICE_URL_PROD = "http://naptien.thanhtoan247.net.vn:8082/CDV_Partner_Services_V1.0/services/Interfaces?wsdl";

    private static final String VNPT_EPAY_KEY_PRIVATE_RSA_UAT = "";
    private static final String VNPT_EPAY_KEY_PRIVATE_RSA_PROD = "";

    // private static final String VNPT_EPAY_KEY_3DES_UAT = "123456abc";
    private static final String VNPT_EPAY_KEY_3DES_UAT = "7B7135E9CA25E5095CAC18BF5D1C58BA";
    private static final String VNPT_EPAY_KEY_3DES_PROD = "7B7135E9CA25E5095CAC18BF5D1C58BA";

    // private static final String VNPT_EPAY_PARTNER_NAME_UAT = "partnerTest";
    private static final String VNPT_EPAY_PARTNER_NAME_UAT = "BLUECOM";
    private static final String VNPT_EPAY_PARTNER_NAME_PROD = "BLUECOM";

    ////////////////////////////////////
    // for get qr recharge VNPT Epay
    // Default Bank information:
    // Vietcombank: 0011002572864
    // CÔNG TY CỔ PHẦN THANH TOÁN ĐIỆN TỬ VNPT
    private static final String VNPT_EPAY_REQUEST_PAYMENT_BANK_TYPE_ID_UAT = "ebd51e4f-6036-431d-a5c8-0dbde770ea0f";
    private static final String VNPT_EPAY_REQUEST_PAYMENT_BANK_TYPE_ID_PROD = "ebd51e4f-6036-431d-a5c8-0dbde770ea0f";

    private static final String VNPT_EPAY_REQUEST_PAYMENT_CAI_UAT = "970436";
    private static final String VNPT_EPAY_REQUEST_PAYMENT_CAI_PROD = "970436";

    private static final String VNPT_EPAY_REQUEST_PAYMENT_BANK_ACCOUNT_UAT = "0011002572864";
    private static final String VNPT_EPAY_REQUEST_PAYMENT_BANK_ACCOUNT_PROD = "0011002572864";

    private static final String VNPT_EPAY_REQUEST_PAYMENT_BANK_USER_NAME_UAT = "CT CP THANH TOAN DIEN TU VNPT";
    private static final String VNPT_EPAY_REQUEST_PAYMENT_BANK_USER_NAME_PROD = "CT CP THANH TOAN DIEN TU VNPT";

    ///////////////////////////////////
    // for recharge into VietQR VN
    private static final String BUSINESS_ID_RECHARGE_UAT = "";
    private static final String BUSINESS_ID_RECHARGE_PROD = "bb6ca0e0-f085-4f03-9166-18cbfc840d63";
    //
    private static final String BRANCH_ID_RECHARGE_UAT = "";
    private static final String BRANCH_ID_RECHARGE_PROD = "3edf55a8-c85b-40d7-b5f9-eb0bf5c1224b";
    //
    private static final String CAI_VALUE_RECHARGE = "970422";
    //
    private static final String BANK_LOGO_ID_RECHARGE_UAT = "58b7190b-a294-4b14-968f-cd365593893e";
    private static final String BANK_LOGO_ID_RECHARGE_PROD = "58b7190b-a294-4b14-968f-cd365593893e";
    //
    private static final String BANK_ID_RECHARGE_UAT = "";
    private static final String BANK_ID_RECHARGE_PROD = "172ff268-6d2e-4bcf-8aaa-1902badfcc63";
    //
    private static final String BANK_ACCOUNT_RECHARGE_UAT = "";
    private static final String BANK_ACCOUNT_RECHARGE_PROD = "0801133666888";
    //
    private static final String BANK_TYPE_ID_RECHARGE_UAT = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
    private static final String BANK_TYPE_ID_RECHARGE_PROD = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";

    ///////////////////////////////////
    //
    private static final String DEFAULT_USER_ID_TEST = "62ad476d-3b6b-4926-9890-fa6a20144f7f";

    // Social Network config
    // Telegram
    private static final String TELEGRAM_BOT_USERNAME = "vietqr_bot";
    private static final String TELEGRAM_BOT_TOKEN = "6603683411:AAGTCde6C-7kt4gR5gTvcDA_jmYIqs0SJJM";

    public static String getQRLink() {
        return (IS_PRODUCTION == false) ? QR_LINK_UAT : QR_LINK_PROD;
    }

    public static String getVNPTEpayRequestPaymentBankUsername() {
        return (IS_PRODUCTION == false) ? VNPT_EPAY_REQUEST_PAYMENT_BANK_USER_NAME_UAT
                : VNPT_EPAY_REQUEST_PAYMENT_BANK_USER_NAME_PROD;
    }

    public static String getVNPTEpayRequestPaymentBankAccount() {
        return (IS_PRODUCTION == false) ? VNPT_EPAY_REQUEST_PAYMENT_BANK_ACCOUNT_UAT
                : VNPT_EPAY_REQUEST_PAYMENT_BANK_ACCOUNT_PROD;
    }

    public static String getVNPTEpayRequestPaymentBankTypeId() {
        return (IS_PRODUCTION == false) ? VNPT_EPAY_REQUEST_PAYMENT_BANK_TYPE_ID_UAT
                : VNPT_EPAY_REQUEST_PAYMENT_BANK_TYPE_ID_PROD;
    }

    public static String getVNPTEpayRequestPaymentCAI() {
        return (IS_PRODUCTION == false) ? VNPT_EPAY_REQUEST_PAYMENT_CAI_UAT : VNPT_EPAY_REQUEST_PAYMENT_CAI_PROD;
    }

    public static String getDefaultUserIdTest() {
        return (IS_PRODUCTION == false) ? DEFAULT_USER_ID_TEST : "";
    }

    public static boolean isProduction() {
        return IS_PRODUCTION;
    }

    public static String getVoiceRequestUrl() {
        return VOICE_REQUEST_URL;
    }

    public static String getAppIdVoice() {
        return APP_ID_VOICE;
    }

    public static String getVoiceType() {
        return VOICE_TYPE;
    }

    public static String getVoiceCode() {
        return VOICE_CODE;
    }

    public static String getSpeedRate() {
        return SPEED_RATE;
    }

    public static int getBitRate() {
        return BIT_RATE;
    }

    public static String getTelegramBotUsername() {
        return TELEGRAM_BOT_USERNAME;
    }

    public static String getTelegramBotToken() {
        return TELEGRAM_BOT_TOKEN;
    }

    public static String getBranchIdRecharge() {
        return (IS_PRODUCTION == false) ? BRANCH_ID_RECHARGE_UAT : BRANCH_ID_RECHARGE_PROD;
    }

    public static String getBusinessIdRecharge() {
        return (IS_PRODUCTION == false) ? BUSINESS_ID_RECHARGE_UAT : BUSINESS_ID_RECHARGE_PROD;
    }

    public static String getCAIRecharge() {
        return CAI_VALUE_RECHARGE;
    }

    public static String getBankLogoIdRecharge() {
        return (IS_PRODUCTION == false) ? BANK_LOGO_ID_RECHARGE_UAT : BANK_LOGO_ID_RECHARGE_PROD;
    }

    public static String getBankIdRecharge() {
        return (IS_PRODUCTION == false) ? BANK_ID_RECHARGE_UAT : BANK_ID_RECHARGE_PROD;
    }

    public static String getBankAccountRecharge() {
        return (IS_PRODUCTION == false) ? BANK_ACCOUNT_RECHARGE_UAT : BANK_ACCOUNT_RECHARGE_PROD;
    }

    public static String getBankTypeIdRecharge() {
        return (IS_PRODUCTION == false) ? BANK_TYPE_ID_RECHARGE_UAT : BANK_TYPE_ID_RECHARGE_PROD;
    }

    public static String getVnptEpayPartnerName() {
        return (IS_PRODUCTION == false) ? VNPT_EPAY_PARTNER_NAME_UAT : VNPT_EPAY_PARTNER_NAME_PROD;
    }

    public static String getVnptEpayKey3DES() {
        return (IS_PRODUCTION == false) ? VNPT_EPAY_KEY_3DES_UAT : VNPT_EPAY_KEY_3DES_PROD;
    }

    public static String getVnptEpayWebServiceUrl() {
        return (IS_PRODUCTION == false) ? VNPT_EPAY_WEB_SERVICE_URL_UAT : VNPT_EPAY_WEB_SERVICE_URL_PROD;
    }

    public static String getVnptEpayKeyPrivateRSA() {
        return (IS_PRODUCTION == false) ? VNPT_EPAY_KEY_PRIVATE_RSA_UAT : VNPT_EPAY_KEY_PRIVATE_RSA_PROD;
    }

    public static String getSftpUsername() {
        return (IS_PRODUCTION == false) ? SFTP_USER_UAT : SFTP_USER_PROD;
    }

    public static String getSftpPassword() {
        return (IS_PRODUCTION == false) ? SFTP_PASSWORD_UAT : SFTP_PASSWORD_PROD;
    }

    public static String getSftpHosting() {
        return (IS_PRODUCTION == false) ? SFTP_HOSTING_UAT : SFTP_HOSTING_PROD;
    }

    public static int getSftpPort() {
        return SFTP_PORT;
    }

    public static String getSftpInputFolder() {
        return SFTP_INPUT_FOLDER;
    }

    public static String getSftpOutputFolder() {
        return SFTP_OUTPUT_FOLDER;
    }

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
