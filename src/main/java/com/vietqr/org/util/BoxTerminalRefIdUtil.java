package com.vietqr.org.util;

import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BoxTerminalRefIdUtil {
    private static final Logger logger = Logger.getLogger(BoxTerminalRefIdUtil.class);
    private static final String VIET_QR_BOX_ACCESS_KEY = "VietQRBoxAccessKey";
//    private static final String VIET_QR_MERCHANT_ACCESS_KEY = "VietQRMerchantAccessKey";

    public static String encode(String input) {
        byte[] encodedBytes = Base64.getUrlEncoder().withoutPadding().encode(input.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    public static String decode(String encodedString) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedString.getBytes(StandardCharsets.UTF_8));
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public static String encryptQrBoxId(String id) {
        String result = "";
        try {
            String encryptedId = encode(id + VIET_QR_BOX_ACCESS_KEY);
            result = encryptedId;
        } catch (Exception e) {
            logger.error("encryptTransactionId: ERROR:" + e.toString());
            System.out.println("encryptTransactionId: ERROR:" + e.toString());
        }
        return result;
    }

//    public static String encryptMerchantId(String id) {
//        String result = "";
//        try {
//            String encryptedId = encode(id + VIET_QR_MERCHANT_ACCESS_KEY);
//            result = encryptedId;
//        } catch (Exception e) {
//            logger.error("encryptTransactionId: ERROR:" + e.toString());
//            System.out.println("encryptTransactionId: ERROR:" + e.toString());
//        }
//        return result;
//    }

    public static String decryptBoxId(String refId) {
        String result = "";
        try {
            result = decode(refId).replaceAll("VietQRBoxAccessKey", "");
        } catch (Exception e) {
            logger.error("decryptTransactionId: ERROR:" + e.toString());
            System.out.println("decryptTransactionId: ERROR:" + e.toString());
        }
        return result;
    }
}
