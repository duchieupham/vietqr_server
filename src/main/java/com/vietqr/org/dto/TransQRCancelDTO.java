package com.vietqr.org.dto;

import java.io.Serializable;

public class TransQRCancelDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String refId;

    public TransQRCancelDTO() {
        super();
    }

    public TransQRCancelDTO(String refId) {
        this.refId = refId;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

}
