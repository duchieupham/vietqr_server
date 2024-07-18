package com.vietqr.org.dto.bidv;

import java.io.Serializable;

public class ResponseReqCustomerVaDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String merchantId;
    private String confirmId;
    private String merchantName;

    public ResponseReqCustomerVaDTO() {
        super();
    }

    public ResponseReqCustomerVaDTO(
            String merchantId,
            String confirmId) {
        super();
        this.merchantId = merchantId;
        this.confirmId = confirmId;
    }

    public ResponseReqCustomerVaDTO(String merchantId, String confirmId, String merchantName) {
        this.merchantId = merchantId;
        this.confirmId = confirmId;
        this.merchantName = merchantName;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getConfirmId() {
        return confirmId;
    }

    public void setConfirmId(String confirmId) {
        this.confirmId = confirmId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}
