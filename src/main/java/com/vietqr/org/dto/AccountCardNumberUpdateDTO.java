package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountCardNumberUpdateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String cardNumber;
    // NULL <> CARD, NFC_CARD
    private String cardType;

    public AccountCardNumberUpdateDTO() {
        super();
    }

    public AccountCardNumberUpdateDTO(String userId, String cardNumber) {
        this.userId = userId;
        this.cardNumber = cardNumber;
    }

    public AccountCardNumberUpdateDTO(String userId, String cardNumber, String cardType) {
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

}
