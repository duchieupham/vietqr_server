package com.vietqr.org.dto.qrfeed;

import java.io.Serializable;

public class QrCreateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fullName;
    private String phoneNo;
    private String email;
    private String userId;
    private String qrName;
    private String qrDescription;
    private String text;
    private String pin;
    private int isPublic;

    public QrCreateRequestDTO() {
        super();
    }

    public QrCreateRequestDTO(String fullName, String phoneNo, String email, String userId, String qrName, String qrDescription, String text, String pin, int isPublic) {
        this.fullName = fullName;
        this.phoneNo = phoneNo;
        this.email = email;
        this.userId = userId;
        this.qrName = qrName;
        this.qrDescription = qrDescription;
        this.text = text;
        this.pin = pin;
        this.isPublic = isPublic;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
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
