package com.vietqr.org.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.vietqr.org.service.vnpt.services.InterfacesSoapBindingStub;
import com.vietqr.org.service.vnpt.services.QueryBalanceResult;
import com.vietqr.org.service.vnpt.services.TopupResult;

// import sun.misc.BASE64Decoder;
// import sun.misc.BASE64Encoder;

public class VNPTEpayUtil {
    public static String private_key;
    public static String public_key;
    private static InterfacesSoapBindingStub service = null;

    // Read key
    public static void initializeKeys() {
        private_key = Readfile("keyRSA/private_key.pem");
        public_key = Readfile("keyRSA/public_key.pem");
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
        }
        return xau;
    }

    public static InterfacesSoapBindingStub getService() throws Exception {
        if (service == null) {
            URL oUrl = new URL(EnvironmentUtil.getVnptEpayWebServiceUrl());
            service = new InterfacesSoapBindingStub(oUrl, null);
            service.setTimeout(300000);
        }
        return service;
    }

    public static String createRequestID(String partnerName) {
        String requestID = "";
        try {
            Date today = new Date();
            Timestamp timeNow = new Timestamp(today.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
            String strDate = dateFormat.format(timeNow);
            Random random = new Random();
            int subResult = 0;
            do {
                subResult = random.nextInt(999999);
            } while (subResult < 100000);
            return partnerName + "_" + strDate + "_" + subResult;
        } catch (Exception e) {
            requestID = "";
            e.printStackTrace();
        }
        return requestID;
    }

    public static void queryBalance(String partnerName) {
        try {
            String keyPrivateRsa = EnvironmentUtil.getVnptEpayKeyPrivateRSA();
            if (keyPrivateRsa == null || keyPrivateRsa.equals("")) {
                initializeKeys();
                keyPrivateRsa = private_key;
            }
            String dataSign = partnerName;
            String sign = sign(dataSign, keyPrivateRsa);
            QueryBalanceResult result = getService().queryBalance(partnerName, sign);
            System.out.println(result.getErrorCode());
            System.out.println(result.getMessage());
            System.out.println("So du tien mat: " + result.getBalance_money());
            System.out.println("So du thuong: " + result.getBalance_bonus());
            System.out.println("So du tam giu: " + result.getBalance_debit());
            System.out.println("So du kha dung: " + result.getBalance_avaiable());
        } catch (Exception e) {
            System.out.println("ERROR AT queryBalance: " + e.toString());
            e.printStackTrace();
        }
    }

    public static String sign(String data, String key_private) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(key_private);
            Security.addProvider(new BouncyCastleProvider());
            PrivateKey privateKey = KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            Signature rsa = Signature.getInstance("SHA1withRSA");
            rsa.initSign(privateKey);
            rsa.update(data.getBytes());
            return Base64.getEncoder().encodeToString(rsa.sign());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void topup(String requestId, String partnerName, String provider, String target, int amount) {
        System.out.println("requestId: " + requestId);
        try {
            String keyPrivateRsa = EnvironmentUtil.getVnptEpayKeyPrivateRSA();
            if (keyPrivateRsa == null || keyPrivateRsa.equals("")) {
                initializeKeys();
                keyPrivateRsa = private_key;
            }
            String dataSign = requestId + partnerName + provider + target + amount;
            System.out.println("Data sign: " + dataSign);
            String sign = sign(dataSign, keyPrivateRsa);
            System.out.println("sign after process: " + sign);
            TopupResult result = getService().topup(requestId, partnerName, provider, target, amount, sign);
            System.out.println("ERROR CODE: " + result.getErrorCode());
            System.out.println("MESSAGE: " + result.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean verify(String data, String sign, String key_public) {
        try {

            byte[] publicKeyBytes = Base64.getDecoder().decode(key_public);
            // byte[] publicKeyBytes = key_public.getBytes();
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            Signature rsa = Signature.getInstance("SHA1withRSA");
            rsa.initVerify(publicKey);
            rsa.update(data.getBytes());
            byte[] signByte = Base64.getDecoder().decode(sign);
            return (rsa.verify(signByte));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // public static String sign(String data, String key_private) throws Exception {
    // System.out.println("KEY PRIVATE: " + key_private);
    // // Get the BouncyCastleProvider
    // Provider bouncyCastleProvider = new BouncyCastleProvider();

    // // Add the BouncyCastleProvider to the list of providers
    // Security.addProvider(bouncyCastleProvider);

    // // Get the private key
    // PrivateKey privateKey = KeyFactory.getInstance("RSA")
    // .generatePrivate(new
    // PKCS8EncodedKeySpec(Base64.getDecoder().decode(key_private)));

    // // Create a Signature object
    // Signature rsa = Signature.getInstance("SHA1withRSA");

    // // Initialize the Signature object
    // rsa.initSign(privateKey);

    // // Update the Signature object with the data
    // rsa.update(data.getBytes());

    // // Sign the data
    // byte[] signature = rsa.sign();

    // // Encode the signature in base64
    // String encodedSignature = Base64.getEncoder().encodeToString(signature);

    // // Return the encoded signature
    // return encodedSignature;
    // }

}
