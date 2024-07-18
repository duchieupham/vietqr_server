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

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.vietqr.org.service.vnpt.services.InterfacesSoapBindingStub;
import com.vietqr.org.service.vnpt.services.QueryBalanceResult;
import com.vietqr.org.service.vnpt.services.TopupResult;

public class VNPTEpayUtil {
    private static final Logger logger = Logger.getLogger(VNPTEpayUtil.class);

    public static String private_key;
    public static String public_key;
    private static InterfacesSoapBindingStub service = null;

    // Read key
    public static void initializeKeys() {
        // private_key = Readfile("keyRSAProd/private_key.pem");
        // public_key = Readfile("keyRSAProd/public_key.pem");
        logger.info("URL FILE PATH: " + "/opt/keyRSAProd/private_key.pem");
        private_key = Readfile("/opt/keyRSAProd/private_key.pem");
        logger.info("URL FILE PATH: " + "/opt/keyRSAProd/public_key.pem");
        public_key = Readfile("/opt/keyRSAProd/public_key.pem");
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
            logger.error("VNPTEpayUtil - ERROR: Readfile: " + e.toString());
        }
        logger.info("FILE: " + xau);
        return xau;
    }

    public static InterfacesSoapBindingStub getService() throws Exception {
        if (service == null) {
            URL oUrl = new URL(EnvironmentUtil.getVnptEpayWebServiceUrl());
            logger.info("oURL: " + oUrl);
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

    public static QueryBalanceResult queryBalance(String partnerName) {
        QueryBalanceResult result = null;
        try {
            String keyPrivateRsa = EnvironmentUtil.getVnptEpayKeyPrivateRSA();
            if (keyPrivateRsa == null || keyPrivateRsa.equals("")) {
                initializeKeys();
                keyPrivateRsa = private_key;
            }
            String dataSign = partnerName;
            String sign = sign(dataSign, keyPrivateRsa);
            logger.info("sign: " + sign);
            result = getService().queryBalance(partnerName, sign);
            logger.info("err code: " + result.getErrorCode());
            logger.info("msg " + result.getMessage());
            logger.info("So du tien mat: " + result.getBalance_money());
            logger.info("So du thuong: " + result.getBalance_bonus());
            logger.info("So du tam giu: " + result.getBalance_debit());
            logger.info("So du kha dung: " + result.getBalance_avaiable());
            //
            System.out.println(result.getErrorCode());
            System.out.println(result.getMessage());
            System.out.println("So du tien mat: " + result.getBalance_money());
            System.out.println("So du thuong: " + result.getBalance_bonus());
            System.out.println("So du tam giu: " + result.getBalance_debit());
            System.out.println("So du kha dung: " + result.getBalance_avaiable());
        } catch (Exception e) {
            System.out.println("ERROR AT queryBalance: " + e.toString());
            logger.error("ERROR AT queryBalance: " + e.toString());
            e.printStackTrace();
        }
        return result;
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

    public static int topup(String requestId, String partnerName, String provider, String target, int amount) {
        int result = 99;
        try {
            String keyPrivateRsa = EnvironmentUtil.getVnptEpayKeyPrivateRSA();
            if (keyPrivateRsa == null || keyPrivateRsa.equals("")) {
                initializeKeys();
                keyPrivateRsa = private_key;
            }
            String dataSign = requestId + partnerName + provider + target + amount;
            System.out.println("topup: data sign: " + dataSign);
            logger.info("topup: data sign: " + dataSign);
            //
            String sign = sign(dataSign, keyPrivateRsa);
            System.out.println("topup: sign after process: " + sign);
            logger.info("topup: sign after process: " + sign);
            //
            TopupResult topupResult = getService().topup(requestId, partnerName, provider, target, amount, sign);
            System.out.println("topup: ERROR CODE: " + topupResult.getErrorCode());
            System.out.println("topup: MESSAGE: " + topupResult.getMessage());
            logger.info("topup: ERROR CODE: " + topupResult.getErrorCode());
            logger.info("topup: MESSAGE: " + topupResult.getMessage());
            //
            result = topupResult.getErrorCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
}