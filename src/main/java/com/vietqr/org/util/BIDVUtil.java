package com.vietqr.org.util;

import com.vietqr.org.dto.ConfirmLinkedBankDTO;
import com.vietqr.org.dto.RequestLinkedBankDTO;

public class BIDVUtil {

    public static String generateRequestLinkedBody(
            String serviceId,
            String merchantId,
            String merchantName,
            String channelId,
            String transDate,
            String payerDebitType,
            String registerSmartbanking,
            RequestLinkedBankDTO dto) {
        String result = "";
        if (dto != null) {
            result = "{";
            result += "\"serviceId\":" + "\"" + serviceId + "\",";
            result += "\"merchantId\":" + "\"" + merchantId + "\",";
            result += "\"merchantName\":" + "\"" + merchantName + "\",";
            result += "\"channelId\":" + "\"" + channelId + "\",";
            result += "\"transDate\":" + "\"" + transDate + "\",";
            result += "\"payerId\":" + "\"" + dto.getAccountNumber() + "\",";
            result += "\"payerName\":" + "\"" + dto.getAccountName() + "\",";
            result += "\"payerIdentity\":" + "\"" + dto.getNationalId() + "\",";
            result += "\"payerMobile\":" + "\"" + dto.getPhoneNumber() + "\",";
            result += "\"payerDebit\":" + "\"" + dto.getAccountNumber() + "\",";
            result += "\"payerDebitType\":" + "\"" + payerDebitType + "\",";
            result += "\"registerSmartbanking\":" + "\"" + registerSmartbanking + "\"";
            result += "}";
        }
        return result;
    }

    public static String generateConfirmLinkedBody(
            String serviceId,
            String merchantId,
            String merchantName,
            String channelId,
            String transDate,
            ConfirmLinkedBankDTO dto) {
        String result = "";
        if (dto != null) {
            result = "{";
            result += "\"serviceId\":" + "\"" + serviceId + "\",";
            result += "\"merchantId\":" + "\"" + merchantId + "\",";
            result += "\"merchantName\":" + "\"" + merchantName + "\",";
            result += "\"channelId\":" + "\"" + channelId + "\",";
            result += "\"transDate\":" + "\"" + transDate + "\",";
            result += "\"payerId\":" + "\"" + dto.getBankAccount() + "\",";
            result += "\"confirmId\":" + "\"" + dto.getRequestId() + "\",";
            result += "\"otpNumber\":" + "\"" + dto.getOtpValue() + "\"";
            result += "}";
        }
        return result;
    }

    public static String generateUnlinkedBody(
            String serviceId,
            String merchantId,
            String merchantName,
            String channelId,
            String transDate,
            String ewalletToken,
            String payerId,
            String unlinkAll) {
        String result = "";
        result = "{";
        result += "\"serviceId\":" + "\"" + serviceId + "\",";
        result += "\"merchantId\":" + "\"" + merchantId + "\",";
        result += "\"merchantName\":" + "\"" + merchantName + "\",";
        result += "\"channelId\":" + "\"" + channelId + "\",";
        result += "\"tranDate\":" + "\"" + transDate + "\",";
        result += "\"ewalletToken\":" + "\"" + ewalletToken + "\",";
        result += "\"payerId\":" + "\"" + payerId + "\",";
        result += "\"unlinkAll\":" + "\"" + unlinkAll + "\"";
        result += "}";
        return result;
    }
}
