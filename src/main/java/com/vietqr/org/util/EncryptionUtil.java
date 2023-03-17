package com.vietqr.org.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptionUtil {
    public static String encode(String input, int length, int limit) {
        if (input.length() > length) {
            input = input.substring(0, length);
        }
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        String hexString = bytesToHex(bytes);
        String encoded = Base64.getEncoder().encodeToString(hexString.getBytes(StandardCharsets.UTF_8));
        if (encoded.length() > limit) {
            encoded = encoded.substring(0, limit);
        }
        return encoded;
    }

    public static String decode(String encoded) {
        byte[] bytes = Base64.getDecoder().decode(encoded);
        String hexString = bytesToHex(bytes);
        String result = hexToString(hexString);
        return result;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }

    public static String hexToString(String hexString) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < hexString.length(); i += 2) {
            String str = hexString.substring(i, i + 2);
            stringBuilder.append((char) Integer.parseInt(str, 16));
        }
        return stringBuilder.toString();
    }
}
