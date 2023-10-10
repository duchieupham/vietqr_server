package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountLoginMethodDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // Method: CARD, NFC_CARD, USER_ID
    private String method;
    private String cardNumber;
    private String userId;
    private String fcmToken;
    private String platform;
    private String device;

    public AccountLoginMethodDTO() {
        super();
    }

    public AccountLoginMethodDTO(String method, String cardNumber, String userId, String fcmToken, String platform,
            String device) {
        super();
        this.method = method;
        this.cardNumber = cardNumber;
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.platform = platform;
        this.device = device;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

}
