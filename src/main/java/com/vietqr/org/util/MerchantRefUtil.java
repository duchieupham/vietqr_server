package com.vietqr.org.util;

import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MerchantRefUtil {
    private static final Logger logger = Logger.getLogger(MerchantRefUtil.class);

    private static final String VIET_QR_MERCHANT_ACCESS_KEY = "VietQRMerchantAccessKey";

    public static String encryptMerchantId(String id) {
        String result = "";
        try {
            String encryptedId = encode(id + VIET_QR_MERCHANT_ACCESS_KEY);
            result = encryptedId;
        } catch (Exception e) {
            logger.error("encryptTransactionId: ERROR:" + e.toString());
            System.out.println("encryptTransactionId: ERROR:" + e.toString());
        }
        return result;
    }

    public static String encode(String input) {
        byte[] encodedBytes = Base64.getUrlEncoder().withoutPadding().encode(input.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    public static String decode(String encodedString) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedString.getBytes(StandardCharsets.UTF_8));
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
