package com.vietqr.org.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class BankEncryptUtil {
    private static final Logger logger = Logger.getLogger(BankEncryptUtil.class);
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 128;
    private static final int ITERATION_COUNT = 1989;
    private static final String SECRET_KEY = "Runsystem!@#2020";
    private static final String ACCESS_KEY_CHECKSUM = "BluecomAccesskey";
    private static final String VIET_QR_KEY_CHECKSUM = "VietQRAccesskey";

    public static boolean isMatchChecksum(String data, String checkSum) {
        return data.equals(checkSum);
    }

    public static String generateRefundMD5Checksum(String SECRET_KEY, String referenceNumber, String amount , String bankAccount) {
        String result = "";
        try {
            String plainText = SECRET_KEY + referenceNumber + amount + bankAccount;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("generateMD5Checksum ERROR: " + e.toString());
        }
        return result;
    }

    public static String generateActiveKeyMD5Checksum(String bankId, String keyActive) {
        String result = "";
        try {
            String plainText = VIET_QR_KEY_CHECKSUM + bankId + keyActive;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("generateMD5Checksum ERROR: " + e.toString());
        }
        return result;
    }

    public static String generateConfirmKeyMD5Checksum(String otp, String keyActive) {
        String result = "";
        try {
            String plainText = VIET_QR_KEY_CHECKSUM + otp + keyActive;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("generateMD5Checksum ERROR: " + e.toString());
        }
        return result;
    }

    public static String generateIdempotencyKey(String referenceNumber, String bankAccount) {
        String result = "";
        try {
            String plainText = referenceNumber + bankAccount;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("generateMD5Checksum ERROR: " + e.toString());
        }
        return result;
    }

    public static String generateCheckOrderMD5Checksum(String traceTransfer, String billNumber, String referenceLabel) {
        String result = "";
        try {
            String plainText = traceTransfer + billNumber + referenceLabel + "BLC" + ACCESS_KEY_CHECKSUM;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("generateMD5Checksum ERROR: " + e.toString());
        }
        return result;
    }

    public static String generateMD5GetBillForBankChecksum(String secretCode, String serviceId, String customerId) {
        String result = "";
        try {
            String plainText = secretCode + serviceId + customerId;
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

    public static String generateMD5GetAccountInfoCheckSum(String accountNumber, String bin,
                                                           String accountType) {
        String result = "";
        try {
            String plainText = bin + accountType + accountNumber + VIET_QR_KEY_CHECKSUM;
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

    public static String generateMD5EcommerceCheckSum(String accessKey,
                                                           String ecommerceSite) {
        String result = "";
        try {
            String plainText = accessKey + ":" + ecommerceSite + VIET_QR_KEY_CHECKSUM;
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

    public static String generateMD5PayBillForBankChecksum(String secretCode,
            String transId,
            String billId,
            String amount) {
        String result = "";
        try {
            String plainText = secretCode + transId + billId + amount;
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

    public static String generateMD5SyncTidChecksum(String accessKey, String bankCode,
                                                           String bankAccount) {
        String result = "";
        try {
            String plainText = accessKey + bankCode + bankAccount;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("generateMD5Checksum ERROR: " + e.toString());
        }
        return result;
    }

    public static String generateMD5SyncMidChecksum(String accessKey, String merchantName,
                                                    String merchantIdentity) {
        String result = "";
        try {
            String plainText = accessKey + merchantName + merchantIdentity;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("generateMD5Checksum ERROR: " + e.toString());
        }
        return result;
    }

    public static String generateMD5RefundCustomerChecksum(String bankAccount, String ftCode,
            String accessKey) {
        String result = "";
        try {
            String plainText = bankAccount + ftCode + accessKey;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("generateMD5Checksum ERROR: " + e.toString());
        }
        return result;
    }

    public static String generateMD5CheckOrderChecksum(String bankAccount,
            String accessKey) {
        String result = "";
        try {
            String plainText = bankAccount + accessKey;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("generateMD5Checksum ERROR: " + e.toString());
        }
        return result;
    }

    public static String generateMD5Checksum(String traceTransfer, String billNumber, String payDate,
            String debitAmount) {
        String result = "";
        try {
            String plainText = traceTransfer + billNumber + payDate + debitAmount + ACCESS_KEY_CHECKSUM;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("generateMD5Checksum ERROR: " + e.toString());
        }
        return result;
    }

    public static String encrypt(String plaintext) throws Exception {
        byte[] iv = generateRandom(IV_SIZE / 8);
        String salt = toHex(generateRandom(KEY_SIZE / 8));

        SecretKey derivedKey = generateKey(salt, SECRET_KEY);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, derivedKey, new IvParameterSpec(iv));

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes("UTF-8"));

        String encryptedBase64 = Base64.getEncoder().encodeToString(ciphertext);

        return salt + toHex(iv) + encryptedBase64;
    }

    public static String decrypt(String encryptedText) throws Exception {
        String salt = encryptedText.substring(0, KEY_SIZE / 4);
        String ivHex = encryptedText.substring(KEY_SIZE / 4, KEY_SIZE / 4 + IV_SIZE / 4);
        String encryptedBase64 = encryptedText.substring(KEY_SIZE / 4 + IV_SIZE / 4);

        byte[] iv = fromHex(ivHex);

        SecretKey derivedKey = generateKey(salt, SECRET_KEY);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, derivedKey, new IvParameterSpec(iv));

        byte[] ciphertext = Base64.getDecoder().decode(encryptedBase64);

        byte[] plaintext = cipher.doFinal(ciphertext);

        return new String(plaintext, "UTF-8");
    }

    private static SecretKey generateKey(String salt, String secretKey) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), fromHex(salt), ITERATION_COUNT, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    private static byte[] generateRandom(int length) {
        byte[] randomBytes = new byte[length];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return randomBytes;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] fromHex(String hexString) {
        int len = hexString.length();
        byte[] result = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            result[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return result;
    }
}