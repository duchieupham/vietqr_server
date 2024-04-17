package com.vietqr.org.util;

import org.apache.log4j.Logger;

import java.security.MessageDigest;

public class BcryptKeyUtil {
    private static final Logger logger = Logger.getLogger(BcryptKeyUtil.class);

    public static String hashKeyActive(String keyValue, String secretKey, int duration) {
        String result = "";
            try {
                String plainText =duration + keyValue + secretKey;
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(plainText.getBytes());
                byte[] digest = md.digest();
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b & 0xff));
                }
                result = sb.toString();
            } catch (Exception e) {
                logger.error("hashKeyActive ERROR: " + e.toString());
            }
        return result;
    }

    public static boolean isMatchKeyValue(String data, String keyAccess) {
        return data.equals(keyAccess);
    }
}
