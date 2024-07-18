package com.vietqr.org.dto.bidv;

import java.io.Serializable;

public class ConfirmCustomerVaDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String merchantId;
    private String merchantName;
    private String confirmId;
    private String otpNumber;

    public ConfirmCustomerVaDTO() {
        super();
    }

    public ConfirmCustomerVaDTO(
            String merchantId,
            String merchantName,
            String confirmId,
            String otpNumber) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.confirmId = confirmId;
        this.otpNumber = otpNumber;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getConfirmId() {
        return confirmId;
    }

    public void setConfirmId(String confirmId) {
        this.confirmId = confirmId;
    }

    public String getOtpNumber() {
        return otpNumber;
    }

    public void setOtpNumber(String otpNumber) {
        this.otpNumber = otpNumber;
    }

}
