package com.vietqr.org.dto;

import java.io.Serializable;

public class TransactionMMSResponseDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String resCode;
    private String resDesc;

    public TransactionMMSResponseDTO() {
        super();
    }

    public TransactionMMSResponseDTO(String resCode, String resDesc) {
        super();
        this.resCode = resCode;
        this.resDesc = resDesc;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }

    @Override
    public String toString() {
        return "TransactionMMSResponseDTO [resCode=" + resCode + ", resDesc=" + resDesc + "]";
    }

}
