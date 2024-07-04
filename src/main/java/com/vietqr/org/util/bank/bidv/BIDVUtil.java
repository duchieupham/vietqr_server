package com.vietqr.org.util.bank.bidv;

import com.vietqr.org.dto.ConfirmLinkedBankDTO;
import com.vietqr.org.dto.RequestLinkedBankDTO;
import com.vietqr.org.dto.bidv.ConfirmCustomerVaDTO;
import com.vietqr.org.dto.bidv.RequestCustomerVaDTO;

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

    public static String generateRequestVaBody(
            String serviceId,
            String channelId,
            String merchantId,
            String merchantName,
            RequestCustomerVaDTO dto,
            String merchantType,
            String tranDate) {
        String result = "";
        result = "{";
        result += "\"serviceId\":" + "\"" + serviceId + "\",";
        result += "\"channelId\":" + "\"" + channelId + "\",";
        result += "\"merchantId\":" + "\"" + merchantId + "\",";
        result += "\"merchantName\":" + "\"" + merchantName + "\",";
        result += "\"accountNo\":" + "\"" + dto.getBankAccount() + "\",";
        result += "\"accountName\":" + "\"" + dto.getUserBankName() + "\",";
        result += "\"identity\":" + "\"" + dto.getNationalId() + "\",";
        result += "\"mobile\":" + "\"" + dto.getPhoneAuthenticated() + "\",";
        result += "\"merchantType\":" + "\"" + merchantType + "\"";
        result += "\"tranDate\":" + "\"" + tranDate + "\"";
        result += "}";
        return result;
    }

    // no need because no need to encrypted confirm add va API
    public static String generateConfirmVaBody(
            String serviceId,
            String channelId,
            ConfirmCustomerVaDTO dto) {
        String result = "";
        result = "{";
        result += "\"serviceId\":" + "\"" + serviceId + "\",";
        result += "\"channelId\":" + "\"" + channelId + "\",";
        result += "\"merchantId\":" + "\"" + dto.getMerchantId() + "\",";
        result += "\"merchantName\":" + "\"" + dto.getMerchantName() + "\",";
        result += "\"confirmId\":" + "\"" + dto.getConfirmId() + "\",";
        result += "\"otpNumber\":" + "\"" + dto.getOtpNumber() + "\"";
        result += "}";
        return result;
    }

    public static String generateUnregisterVaBody(
            String serviceId,
            String channelId,
            String merchantId) {
        String result = "";
        result = "{";
        result += "\"serviceId\":" + "\"" + serviceId + "\",";
        result += "\"channelId\":" + "\"" + channelId + "\",";
        result += "\"merchantId\":" + "\"" + merchantId + "\"";
        result += "}";
        return result;
    }

    public static String generateVietQRBody(
            String serviceId,
            String code,
            String name,
            String amount,
            String description) {
        String result = "";
        result = "{";
        result += "\"serviceId\":" + "\"" + serviceId + "\",";
        result += "\"code\":" + "\"" + code + "\",";
        result += "\"name\":" + "\"" + name + "\",";
        result += "\"amount\":" + "\"" + amount + "\",";
        result += "\"description\":" + "\"" + description + "\"";
        result += "}";
        return result;
    }
}
