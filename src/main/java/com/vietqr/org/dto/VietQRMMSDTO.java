package com.vietqr.org.dto;

import java.io.Serializable;

public class VietQRMMSDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String qrCode;

    public VietQRMMSDTO() {
        super();
    }

    public VietQRMMSDTO(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

}
