package com.vietqr.org.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.log4j.Logger;

public class TransactionRefIdUtil {
    private static final Logger logger = Logger.getLogger(TransactionRefIdUtil.class);
    // private static final String ENCRYPTION_ALGORITHM = "DESede";
    // private static final String secretKey = "VIETQRVNBNSVIETQRVNBNSVIET";

    // public static String encryptTransactionId(String id) {
    // String result = "";
    // try {
    // byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    // DESedeKeySpec spec = new DESedeKeySpec(keyBytes);
    // SecretKeyFactory keyFactory =
    // SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM);
    // SecretKey key = keyFactory.generateSecret(spec);

    // Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
    // cipher.init(Cipher.ENCRYPT_MODE, key);

    // byte[] encryptedBytes = cipher.doFinal(id.getBytes(StandardCharsets.UTF_8));
    // String encryptedId = Base64.getEncoder().encodeToString(encryptedBytes);
    // result = encryptedId;
    // } catch (Exception e) {
    // logger.error("encryptTransactionId: ERROR:" + e.toString());
    // System.out.println("encryptTransactionId: ERROR:" + e.toString());
    // }
    // return result;
    // }

    // public static String decryptTransactionId(String refId) {
    // String result = "";
    // try {
    // byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    // DESedeKeySpec spec = new DESedeKeySpec(keyBytes);
    // SecretKeyFactory keyFactory =
    // SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM);
    // SecretKey key = keyFactory.generateSecret(spec);

    // Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
    // cipher.init(Cipher.DECRYPT_MODE, key);

    // byte[] encryptedBytes = Base64.getDecoder().decode(refId);
    // byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
    // String decryptedId = new String(decryptedBytes, StandardCharsets.UTF_8);
    // result = decryptedId;
    // } catch (Exception e) {
    // logger.error("decryptTransactionId: ERROR:" + e.toString());
    // System.out.println("decryptTransactionId: ERROR:" + e.toString());
    // }
    // return result;
    // }
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