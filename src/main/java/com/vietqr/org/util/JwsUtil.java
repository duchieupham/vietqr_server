package com.vietqr.org.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Scanner;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public class JwsUtil {

    static String path = "keyRSABIDVUAT/";

    public static byte[] hexStringToBytes(String s) {
        byte[] ans = new byte[s.length() / 2];

        for (int i = 0; i < ans.length; i++) {
            int index = i * 2;

            // Using parseInt() method of Integer class
            int val = Integer.parseInt(s.substring(index, index + 2), 16);
            ans[i] = (byte) val;
        }

        return ans;
    }

    public static PrivateKey getPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        KeyFactory factory = KeyFactory.getInstance("RSA");
        // File file = new File("src/main/resources/cert/privatekey.key");
        File file = new File(path + "key.pem");
        try (FileReader keyReader = new FileReader(file); PemReader pemReader = new PemReader(keyReader)) {
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(content);
            PrivateKey privateKey = factory.generatePrivate(privateKeySpec);
            return privateKey;
        }
    }

    public static String getSymmatricKey() throws IOException {
        String key = "";
        try {
            File file = new File(path + "symmatrickey.txt");
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                key = data;
                System.out.println(data);
                break;
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("getSymmatricKey: ERROR: " + e.toString());
        }
        System.out.println("SymmatricKey = " + key);
        return key;
    }

    public static String getPayLoad() throws IOException {
        String payload = "";

        StringBuilder sb = new StringBuilder();

        try {
            // File file = new File(path + "body.txt");
            File file = new File(path + "body-confirm-2.txt");
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                sb.append(myReader.nextLine());
            }
            myReader.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        payload = sb.toString();
        System.out.println("Body: " + payload);

        return payload;
    }
}
