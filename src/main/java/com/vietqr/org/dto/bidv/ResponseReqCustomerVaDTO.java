package com.vietqr.org.dto.bidv;

import java.io.Serializable;

public class ResponseReqCustomerVaDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String merchantId;
    private String confirmId;

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

}
