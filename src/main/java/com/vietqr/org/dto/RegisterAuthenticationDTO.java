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

    public RegisterAuthenticationDTO() {
        super();
    }

    public RegisterAuthenticationDTO(String bankId, String nationalId, String phoneAuthenticated) {
        this.bankId = bankId;
        this.nationalId = nationalId;
        this.phoneAuthenticated = phoneAuthenticated;
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

}
