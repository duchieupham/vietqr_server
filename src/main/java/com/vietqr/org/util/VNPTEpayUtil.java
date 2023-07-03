package com.vietqr.org.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class VNPTEpayUtil {

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
}
