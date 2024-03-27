package com.vietqr.org.util;

public class EnvironmentUtil {
    private static boolean IS_PRODUCTION = true;

    ///
    // BIDV
    // request - confirm linked bank Account
    private static final String BIDV_URL_GET_TOKEN_UAT = "https://bidv.net:9303/bidvorg/service/openapi/oauth2/token";
    private static final String BIDV_URL_GET_TOKEN_PROD = "https://bidv.net:9303/bidvorg/service/openapi/oauth2/token";

    private static final String BIDV_LINKED_URL_REQUEST_UAT = "https://www.bidv.net:9303/bidvorg/service/open-banking/create-ewallet-link-collection/v1";
    private static final String BIDV_LINKED_URL_REQUEST_PROD = "https://www.bidv.net:9303/bidvorg/service/open-banking/create-ewallet-link-collection/v1";

    private static final String BIDV_LINKED_URL_CONFIRM_UAT = "https://www.bidv.net:9303/bidvorg/service/open-banking/confirm-ewallet-link-collection/v1";
    private static final String BIDV_LINKED_URL_CONFIRM_PROD = "https://www.bidv.net:9303/bidvorg/service/open-banking/confirm-ewallet-link-collection/v1";

    private static final String BIDV_UNLINKED_URL_UAT = "https://www.bidv.net:9303/bidvorg/service/open-banking/remove-ewallet-link-collection/v1";
    private static final String BIDV_UNLINKED_URL_PROD = "https://www.bidv.net:9303/bidvorg/service/open-banking/remove-ewallet-link-collection/v1";

    private static final String BIDV_GET_TOKEN_CLIENT_ID_UAT = "c87962d6c3bf521c4f0911589863ff06";
    private static final String BIDV_GET_TOKEN_CLIENT_ID_PROD = "c87962d6c3bf521c4f0911589863ff06";

    private static final String BIDV_GET_TOKEN_CLIENT_SECRET_UAT = "ee2d23a34ce3cdc2a4c3d6d5b794fd3c";
    private static final String BIDV_GET_TOKEN_CLIENT_SECRET_PROD = "ee2d23a34ce3cdc2a4c3d6d5b794fd3c";

    private static final String BIDV_LINKED_SERVICE_ID_UAT = "BC0001";
    private static final String BIDV_LINKED_SERVICE_ID_PROD = "BC0001";

    private static final String BIDV_LINKED_MERCHANT_ID_UAT = "BC0001";
    private static final String BIDV_LINKED_MERCHANT_ID_PROD = "BC0001";

    private static final String BIDV_LINKED_MERCHANT_NAME_UAT = "BLUECOM";
    private static final String BIDV_LINKED_MERCHANT_NAME_PROD = "BLUECOM";

    private static final String BIDV_LINKED_CHANNEL_ID_UAT = "211701";
    private static final String BIDV_LINKED_CHANNEL_ID_PROD = "211701";

    // BIDV
    // request - confirm add merchant va
    private static final String BIDV_URL_REQUEST_ADD_MERCHANT_UAT = "https://www.bidv.net:9303/bidvorg/service/open-banking/paygate/createVAQLBH/v1";
    private static final String BIDV_URL_REQUEST_ADD_MERCHANT_PROD = "https://www.bidv.net:9303/bidvorg/service/open-banking/paygate/createVAQLBH/v1";

    private static final String BIDV_URL_CONFIRM_ADD_MERCHANT_UAT = "https://www.bidv.net:9303/bidvorg/service/open-banking/paygate/confirmVAQLBH/v1";
    private static final String BIDV_URL_CONFIRM_ADD_MERCHANT_PROD = "https://www.bidv.net:9303/bidvorg/service/open-banking/paygate/confirmVAQLBH/v1";

    private static final String BIDV_URL_UNREGISTER_MERCHANT_UAT = "https://www.bidv.net:9303/bidvorg/service/open-banking/paygate/deleteVAQLBH/v1";
    private static final String BIDV_URL_UNREGISTER_MERCHANT_PROD = "https://www.bidv.net:9303/bidvorg/service/open-banking/paygate/deleteVAQLBH/v1";

    private static final String BIDV_LINKED_PAYER_DEBIT_TYPE_ACC_UAT = "810";
    private static final String BIDV_LINKED_PAYER_DEBIT_TYPE_ACC_PROD = "810";
    // private static final String BIDV_LINKED_PAYER_DEBIT_TYPE_CARD_PROD = "810";
    // private static final String BIDV_LINKED_PAYER_DEBIT_TYPE_CARD_PROD = "810";

    // BIDV
    // authentication for Get Bill & Pay Bill
    private static final String BIDV_SECRET_KEY = "QklEVkJMVUVDT01BY2Nlc3NLZXk=";
    private static final String BIDV_ACCESS_KEY = "bank-bidv-0002";

    ///
    // IP ADDRESS
    private static final String IP_VIETQRVN_UAT = "112.78.1.220";
    private static final String IP_VIETQRVN_PROD = "112.78.1.209";

    // URL VIETQR VN
    private static final String URL_VIETQR_VN_UAT = "https://dev.vietqr.org/vqr/api";
    private static final String URL_VIETQR_VN_PROD = "https://api.vietqr.org/vqr/api";

    /// NEWSFEED
    // LINK NEWS IMAGE
    private static final String IMAGE_POST_UAT_LINK = "";
    private static final String IMGAGE_POST_PROD_LINK = "";

    // PARTNERS CONNECT
    private static final String SERVICE_VHITEK_ACTIVE = "VHITEK_ACTIVE";

    // BANK ID TEST IOT
    private static final String BANK_ID_TEST_IOT = "b93246bb-3b94-492e-a774-5ef50c4b619d";

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

    private static final String MERCHANT_ID_QR_BOX_DEFAULT_UAT = "";
    private static final String MERCHANT_ID_QR_BOX_DEFAULT_PROD = "f6eab070-e370-11ee-8355-c43772815012";

    ////////////////////////////////////
    // for get qr recharge VNPT Epay
    // OLD
    // Default Bank information:
    // Vietcombank: 0011002572864
    // CÔNG TY CỔ PHẦN THANH TOÁN ĐIỆN TỬ VNPT

    // NEW
    // Default Bank information:
    // Vietcombank: 0011002572864
    // CÔNG TY CỔ PHẦN THANH TOÁN ĐIỆN TỬ VNPT
    private static final String VNPT_EPAY_REQUEST_PAYMENT_BANK_TYPE_ID_UAT = "332d84ed-bb2e-4ae2-98a8-0c2e6533cad5";
    private static final String VNPT_EPAY_REQUEST_PAYMENT_BANK_TYPE_ID_PROD = "332d84ed-bb2e-4ae2-98a8-0c2e6533cad5";

    private static final String VNPT_EPAY_REQUEST_PAYMENT_CAI_UAT = "970457";
    private static final String VNPT_EPAY_REQUEST_PAYMENT_CAI_PROD = "970457";

    private static final String VNPT_EPAY_REQUEST_PAYMENT_BANK_ACCOUNT_UAT = "902002261912";
    private static final String VNPT_EPAY_REQUEST_PAYMENT_BANK_ACCOUNT_PROD = "902002261912";

    private static final String VNPT_EPAY_REQUEST_PAYMENT_BANK_USER_NAME_UAT = "VNPT EPAY CONG TY CO PHAN BLUECOM VIET NAM";
    private static final String VNPT_EPAY_REQUEST_PAYMENT_BANK_USER_NAME_PROD = "VNPT EPAY CONG TY CO PHAN BLUECOM VIET NAM";

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
    private static final String BANK_ID_RECHARGE_PROD = "bb25279e-b7e6-405e-bdce-a697794f465f";
    //
    private static final String BANK_ACCOUNT_RECHARGE_UAT = "";
    private static final String BANK_ACCOUNT_RECHARGE_PROD = "0020108679008";
    //
    private static final String BANK_TYPE_ID_RECHARGE_UAT = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";
    private static final String BANK_TYPE_ID_RECHARGE_PROD = "aa4e489b-254e-4351-9cd4-f62e09c63ebc";

    private static final String USER_ID_HOST_RECHARGE_UAT = "";
    private static final String USER_ID_HOST_RECHARGE_PROD = "6e3712ab-48ea-4bfa-a50d-f7dd1f184efb";

    // VIET QR ICON ID
    private static final String VIET_QR_ICON_UAT = "f628e7b4-e65c-4b36-a4a8-e53bd4c7e372";
    private static final String VIET_QR_ICON_PROD = "a50ee10a-d248-42ec-95f1-5ec322cfdf8e";

    // VIET QR LOGO ID
    private static final String VIET_QR_LOGO_UAT = "01d9a285-c201-4adc-bdca-f1b31104b12b";
    private static final String VIET_QR_LOGO_PROD = "115a2a25-994b-4e53-bac9-64e6fc57651b";

    // NAPAS LOGO ID
    private static final String NAPAS_LOGO_UAT = "012c5110-0da8-457e-b737-93f4443d98d0";
    private static final String NAPAS_LOGO_PROD = "596da7a9-9a9b-408a-b7dc-220301424ba1";

    // VIET QR URL API
    private static final String VIET_QR_URL_API_UAT = "https://dev.vietqr.org/vqr/api/images/";
    private static final String VIET_QR_URL_API_PROD = "https://api.vietqr.org/vqr/api/images/";

    ///////////////////////////////////
    //
    private static final String DEFAULT_USER_ID_TEST = "62ad476d-3b6b-4926-9890-fa6a20144f7f";

    // Social Network config
    // Telegram
    private static final String TELEGRAM_BOT_USERNAME = "vietqr_bot";
    private static final String TELEGRAM_BOT_TOKEN = "6603683411:AAGTCde6C-7kt4gR5gTvcDA_jmYIqs0SJJM";

    // Google Chat for task push notification
    private static final String GOOGLE_CHAT_KEY = "AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI";
    private static final String GOOGLE_CHAT_TOKEN = "q9cgRDssTNVRgIQCYkfq06Sfh8nS-h4RD3Nrfby9NJk";

    // Static VietQR from MB
    private static final String MB_QR_CODE_STATIC_TYPE = "1";
    private static final String MB_QR_INIT_STATIC_METHOD = "11";

    // Role for member in merchant
    private static final String ONLY_READ_RECEIVE_MERCHANT_ROLE_ID_UAT = "ce30f63e-e5c4-11ee-abd2-c437724afb36";
    private static final String ONLY_READ_RECEIVE_ROLE_ID_PROD = "ce30f63e-e5c4-11ee-abd2-c437724afb36";

    private static final String READ_APPROVE_RECEIVE_MERCHANT_ROLE_ID_UAT = "f63ddf03-e5c4-11ee-abd2-c437724afb36";
    private static final String READ_REQUEST_RECEIVE_ROLE_ID_PROD = "f63ddf03-e5c4-11ee-abd2-c437724afb36";

    private static final String ADMIN_ROLE_ID_UAT = "b9611ed2-e5c4-11ee-abd2-c437724afb36";
    private static final String ADMIN_ROLE_ID_PROD = "b9611ed2-e5c4-11ee-abd2-c437724afb36";

    //Role for member in terminal
    private static final String ONLY_READ_RECEIVE_TERMINAL_ROLE_ID_UAT = "0f470d54-63a3-4cce-8691-5ec9380855f3";
    private static final String ONLY_READ_RECEIVE_TERMINAL_ROLE_ID_PROD = "0f470d54-63a3-4cce-8691-5ec9380855f3";
    private static final String READ_REQUEST_RECEIVE_TERMINAL_ROLE_ID_UAT = "1bf18675-e063-4070-a5d6-14add8cfb518";
    private static final String READ_REQUEST_RECEIVE_TERMINAL_ROLE_ID_PROD = "1bf18675-e063-4070-a5d6-14add8cfb518";

    public static String getBidvSecretKey() {
        return BIDV_SECRET_KEY;
    }

    public static String getBidvAccessKey() {
        return BIDV_ACCESS_KEY;
    }

    public static String getBidvUrlRequestAddMerchant() {
        return (IS_PRODUCTION == false) ? BIDV_URL_REQUEST_ADD_MERCHANT_UAT : BIDV_URL_REQUEST_ADD_MERCHANT_PROD;
    }

    public static final String getBidvUrlConfirmAddMerchant() {
        return (IS_PRODUCTION == false) ? BIDV_URL_CONFIRM_ADD_MERCHANT_UAT : BIDV_URL_CONFIRM_ADD_MERCHANT_PROD;
    }

    public static final String getBidvUrlUnregisterMerchant() {
        return (IS_PRODUCTION == false) ? BIDV_URL_UNREGISTER_MERCHANT_UAT : BIDV_URL_UNREGISTER_MERCHANT_PROD;
    }

    public static String getMbQrCodeStaticType() {
        return MB_QR_CODE_STATIC_TYPE;
    }

    public static String getMbQrInitStaticMethod() {
        return MB_QR_INIT_STATIC_METHOD;
    }

    public static String getIpVietQRVN() {
        return (IS_PRODUCTION == false) ? IP_VIETQRVN_PROD : IP_VIETQRVN_UAT;
    }

    // BIDV BIDV_UNLINKED_URL_UAT
    public static String getBidvUrlUnlinked() {
        return (IS_PRODUCTION == false) ? BIDV_UNLINKED_URL_PROD : BIDV_UNLINKED_URL_UAT;
    }

    public static String getBidvUrlGetToken() {
        return (IS_PRODUCTION == false) ? BIDV_URL_GET_TOKEN_PROD : BIDV_URL_GET_TOKEN_UAT;
    }

    public static String getBidvUrlLinkedRequest() {
        return (IS_PRODUCTION == false) ? BIDV_LINKED_URL_REQUEST_PROD : BIDV_LINKED_URL_REQUEST_UAT;
    }

    public static String getBidvUrlLinkedConfirm() {
        return (IS_PRODUCTION == false) ? BIDV_LINKED_URL_CONFIRM_PROD : BIDV_LINKED_URL_CONFIRM_UAT;
    }

    public static String getBidvGetTokenClientId() {
        return (IS_PRODUCTION == false) ? BIDV_GET_TOKEN_CLIENT_ID_PROD : BIDV_GET_TOKEN_CLIENT_ID_UAT;
    }

    public static String getBidvGetTokenClientSecret() {
        return (IS_PRODUCTION == false) ? BIDV_GET_TOKEN_CLIENT_SECRET_PROD : BIDV_GET_TOKEN_CLIENT_SECRET_UAT;
    }

    public static String getBidvLinkedServiceId() {
        return (IS_PRODUCTION == false) ? BIDV_LINKED_SERVICE_ID_PROD : BIDV_LINKED_SERVICE_ID_UAT;
    }

    public static final String getBidvLinkedMerchantId() {
        return (IS_PRODUCTION == false) ? BIDV_LINKED_MERCHANT_ID_PROD : BIDV_LINKED_MERCHANT_ID_UAT;
    }

    public static String getBidvLinkedMerchantName() {
        return (IS_PRODUCTION == false) ? BIDV_LINKED_MERCHANT_NAME_PROD : BIDV_LINKED_MERCHANT_NAME_UAT;
    }

    public static String getBidvLinkedChannelId() {
        return (IS_PRODUCTION == false) ? BIDV_LINKED_CHANNEL_ID_PROD : BIDV_LINKED_CHANNEL_ID_UAT;
    }

    public static String getBidvLinkedPayerDebitTypeAcc() {
        return (IS_PRODUCTION == false) ? BIDV_LINKED_PAYER_DEBIT_TYPE_ACC_PROD : BIDV_LINKED_PAYER_DEBIT_TYPE_ACC_UAT;
    }

    ///

    public static String getUrlVietqrVnUat() {
        return URL_VIETQR_VN_UAT;
    }

    public static String getUrlVietqrVnProd() {
        return URL_VIETQR_VN_PROD;
    }

    public static String getBankIdTestIOT() {
        return BANK_ID_TEST_IOT;
    }

    public static String getServiceVhitekActive() {
        return SERVICE_VHITEK_ACTIVE;
    }

    public static String getUserIdHostRecharge() {
        return (IS_PRODUCTION == false) ? USER_ID_HOST_RECHARGE_UAT : USER_ID_HOST_RECHARGE_PROD;
    }

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

    public static String getGoogleChatKey() {
        return GOOGLE_CHAT_KEY;
    }

    public static String getGoogleChatToken() {
        return GOOGLE_CHAT_TOKEN;
    }

    public static String getVietQrIcon() {
        return (IS_PRODUCTION == false) ? VIET_QR_ICON_UAT : VIET_QR_ICON_PROD;
    }

    public static String getVietQrLogo() {
        return (IS_PRODUCTION == false) ? VIET_QR_LOGO_UAT : VIET_QR_LOGO_PROD;
    }

    public static String getNapasLogo() {
        return (IS_PRODUCTION == false) ? NAPAS_LOGO_UAT : NAPAS_LOGO_PROD;
    }

    public static String getVietQrUrlApi() {
        return (IS_PRODUCTION == false) ? VIET_QR_URL_API_UAT : VIET_QR_URL_API_PROD;
    }

    public static String getDefaultCustomerSyncIdIot() {
        return (IS_PRODUCTION == false) ? MERCHANT_ID_QR_BOX_DEFAULT_UAT : MERCHANT_ID_QR_BOX_DEFAULT_PROD;
    }

    public static String getOnlyReadReceiveMerchantRoleId() {
        return (IS_PRODUCTION == false) ? ONLY_READ_RECEIVE_MERCHANT_ROLE_ID_UAT : ONLY_READ_RECEIVE_ROLE_ID_PROD;
    }

    public static String getRequestReceiveMerchantRoleId() {
        return (IS_PRODUCTION == false) ? READ_APPROVE_RECEIVE_MERCHANT_ROLE_ID_UAT : READ_REQUEST_RECEIVE_ROLE_ID_PROD;
    }

    public static String getAdminRoleId() {
        return (IS_PRODUCTION == false) ? ADMIN_ROLE_ID_UAT : ADMIN_ROLE_ID_PROD;
    }

    public static String getOnlyReadReceiveTerminalRoleId() {
        return (IS_PRODUCTION == false) ? ONLY_READ_RECEIVE_TERMINAL_ROLE_ID_UAT : ONLY_READ_RECEIVE_TERMINAL_ROLE_ID_PROD;
    }

    public static String getRequestReceiveTerminalRoleId() {
        return (IS_PRODUCTION == false) ? READ_REQUEST_RECEIVE_TERMINAL_ROLE_ID_UAT : READ_REQUEST_RECEIVE_TERMINAL_ROLE_ID_PROD;
    }
}
