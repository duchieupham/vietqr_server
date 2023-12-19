package com.vietqr.org.dto;

import java.io.Serializable;

public class RegisterAuthenticationDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankId;
    private String nationalId;
    private String phoneAuthenticated;
    private String bankAccountName;
    private String bankAccount;
    private String ewalletToken;

    public RegisterAuthenticationDTO() {
        super();
    }

    public RegisterAuthenticationDTO(String bankId, String nationalId, String phoneAuthenticated,
            String bankAccountName, String bankAccount, String ewalletToken) {
        this.bankId = bankId;
        this.nationalId = nationalId;
        this.phoneAuthenticated = phoneAuthenticated;
        this.bankAccount = bankAccount;
        this.bankAccountName = bankAccountName;
        this.ewalletToken = ewalletToken;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getEwalletToken() {
        return ewalletToken;
    }

    public void setEwalletToken(String ewalletToken) {
        this.ewalletToken = ewalletToken;
    }

}
