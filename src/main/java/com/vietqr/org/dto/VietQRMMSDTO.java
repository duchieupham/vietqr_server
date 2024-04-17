package com.vietqr.org.dto;

import java.io.Serializable;

public class VietQRMMSDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String qrCode;
    private String transactionRefId;
    private String qrLink;

    public VietQRMMSDTO() {
        super();
    }

    public VietQRMMSDTO(String qrCode) {
        this.qrCode = qrCode;
    }

    public VietQRMMSDTO(String qrCode, String transactionRefId, String qrLink) {
        this.qrCode = qrCode;
        this.transactionRefId = transactionRefId;
        this.qrLink = qrLink;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getTransactionRefId() {
        return transactionRefId;
    }

    public void setTransactionRefId(String transactionRefId) {
        this.transactionRefId = transactionRefId;
    }

    public String getQrLink() {
        return qrLink;
    }

    public void setQrLink(String qrLink) {
        this.qrLink = qrLink;
    }

}
