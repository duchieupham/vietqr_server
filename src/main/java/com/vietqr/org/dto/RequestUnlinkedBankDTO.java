package com.vietqr.org.dto;

import java.io.Serializable;

public class RequestUnlinkedBankDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String ewalletToken;
    private String bankAccount;
    private String bankCode;

    public RequestUnlinkedBankDTO() {
        super();
    }

    public RequestUnlinkedBankDTO(String ewalletToken, String bankAccount, String bankCode) {
        this.ewalletToken = ewalletToken;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
    }

    public String getEwalletToken() {
        return ewalletToken;
    }

    public void setEwalletToken(String ewalletToken) {
        this.ewalletToken = ewalletToken;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

}
