package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class RegisterAuthenticationDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String bankId;
    @NotBlank
    private String nationalId;
    @NotBlank
    private String phoneAuthenticated;
    @NotBlank
    private String bankAccountName;
    @NotBlank
    private String bankAccount;

    private String ewalletToken;

    private String merchantId;

    private String merchantName;

    private String vaNumber;

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

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getVaNumber() {
        return vaNumber;
    }

    public void setVaNumber(String vaNumber) {
        this.vaNumber = vaNumber;
    }
}
