package com.vietqr.org.util;

import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BoxTerminalRefIdUtil {
    private static final Logger logger = Logger.getLogger(BoxTerminalRefIdUtil.class);
    public static String encode(String input) {
        byte[] encodedBytes = Base64.getUrlEncoder().withoutPadding().encode(input.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    public static String decode(String encodedString) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedString.getBytes(StandardCharsets.UTF_8));
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public static String encryptTransactionId(String id) {
        String result = "";
        try {
            String encryptedId = encode(id);
            result = encryptedId;
        } catch (Exception e) {
            logger.error("encryptTransactionId: ERROR:" + e.toString());
            System.out.println("encryptTransactionId: ERROR:" + e.toString());
        }
        return result;
    }

    public static String decryptTransactionId(String refId) {
        String result = "";
        try {
            result = decode(refId);
        } catch (Exception e) {
            logger.error("decryptTransactionId: ERROR:" + e.toString());
            System.out.println("decryptTransactionId: ERROR:" + e.toString());
        }
        return result;
    }
}
