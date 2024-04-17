package com.vietqr.org.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class BankRSAUtil {
    private static final Logger logger = Logger.getLogger(BankRSAUtil.class);

    public static String private_key;
    public static String public_key;

    // Read key
    public static void initializeKeys() {
        private_key = Readfile("keyRSAMB/private_key.pem");
        public_key = Readfile("keyRSAMB/public_key.pem");
        // logger.info("URL FILE PATH: " + "/opt/keyRSAProd/private_key.pem");
        // private_key = Readfile("/opt/keyRSAProd/private_key.pem");
        // logger.info("URL FILE PATH: " + "/opt/keyRSAProd/public_key.pem");
        // public_key = Readfile("/opt/keyRSAProd/public_key.pem");
    }

    private static String Readfile(String path) {
        String xau = "";
        try {
            FileInputStream fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                xau += strLine + " ";
            }
            xau = xau.trim();
            xau = xau.replace(" ", "\n");
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("BankRSAUtil - ERROR: Readfile: " + e.toString());
        }
        logger.info("FILE: " + xau);
        return xau;
    }

    public static String generateSignature(String data) {
        try {
            String keyPrivateRsa = "";
            if (keyPrivateRsa == null || keyPrivateRsa.equals("")) {
                initializeKeys();
                keyPrivateRsa = private_key;
            }
            byte[] privateKeyBytes = Base64.getDecoder().decode(keyPrivateRsa);
            Security.addProvider(new BouncyCastleProvider());
            PrivateKey privateKey = KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initSign(privateKey);
            rsa.update(data.getBytes());
            return Base64.getEncoder().encodeToString(rsa.sign());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean verifySignature(String data, String signature) {
        try {
            String publicKeyRsa = "";
            if (publicKeyRsa == null || publicKeyRsa.equals("")) {
                initializeKeys();
                publicKeyRsa = public_key;
            }
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyRsa);
            PublicKey publicKey = KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initVerify(publicKey);
            rsa.update(data.getBytes());
            byte[] signatureBytes = Base64.getDecoder().decode(signature);
            return rsa.verify(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
