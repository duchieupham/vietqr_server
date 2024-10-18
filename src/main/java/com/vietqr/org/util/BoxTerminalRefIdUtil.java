package com.vietqr.org.util;

import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
        String result = "";
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedString.getBytes(StandardCharsets.UTF_8));
            String value = new String(decodedBytes, StandardCharsets.UTF_8);
            result = value.replaceAll(VIET_QR_BOX_ACCESS_KEY, "");
        } catch (Exception e) {
            logger.error("decode: ERROR:" + e.getMessage());
        }
        return result;

    }

    public static String encryptQrBoxId(String id) {
        String result = "";
        try {
            String encryptedId = encode(id + VIET_QR_BOX_ACCESS_KEY);
            result = encryptedId;
        } catch (Exception e) {
            logger.error("encryptTransactionId: ERROR:" + e.toString());
            //System.out.println("encryptTransactionId: ERROR:" + e.toString());
        }
        return result;
    }

//    public static String encryptMacAddr(String macAddr) {
//        String result = "";
//        try {
//            String encryptedId = encode(macAddr + VIET_QR_BOX_ACCESS_KEY);
//            result = encryptedId;
//        } catch (Exception e) {
//            logger.error("encryptTransactionId: ERROR:" + e.toString());
//            //System.out.println("encryptTransactionId: ERROR:" + e.toString());
//        }
//        return result;
//    }

    public static String encryptMacAddr(String macAddr) {
        String result = "";
        try {
            String plainText = macAddr + VIET_QR_BOX_ACCESS_KEY;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("generateMD5GetBillForBankChecksum: ERROR: " + e.toString());
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
//            //System.out.println("encryptTransactionId: ERROR:" + e.toString());
//        }
//        return result;
//    }

    public static String decryptBoxId(String refId) {
        String result = "";
        try {
            result = decode(refId).replaceAll("VietQRBoxAccessKey", "");
        } catch (Exception e) {
            logger.error("decryptTransactionId: ERROR:" + e.toString());
            //System.out.println("decryptTransactionId: ERROR:" + e.toString());
        }
        return result;
    }
}
