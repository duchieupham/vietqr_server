package com.vietqr.org.dto.qrfeed;

import java.io.Serializable;

public class QrCreateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String qrName;
    private String qrDescription;
    private String text;
    private String pin;

    public QrCreateRequestDTO() {
        super();
    }

    public QrCreateRequestDTO(String userId, String qrName, String qrDescription, String text, String pin) {
        this.userId = userId;
        this.qrName = qrName;
        this.qrDescription = qrDescription;
        this.text = text;
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public String getQrDescription() {
        return qrDescription;
    }

    public void setQrDescription(String qrDescription) {
        this.qrDescription = qrDescription;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
