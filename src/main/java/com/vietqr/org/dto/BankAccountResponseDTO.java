package com.vietqr.org.dto;

public class BankAccountResponseDTO {
    private String bankId;
    private String bankAccount;
    private String bankAccountName;
    private String bankShortName;
    private String phoneAuthenticated;
    private boolean mmsActive;
    private String nationalId;
    private Long validFeeTo;
    private Long validFrom;
    private Long timeCreate;
    private String phoneNo;
    private String email;
    private boolean status;
    private String bankCode;
    private boolean isValidService;
    private boolean isAuthenticated;
    private int bankTypeStatus;


    private String vso;

    // Constructors, getters and setters

    public BankAccountResponseDTO() {
    }

    public BankAccountResponseDTO(String bankId, String bankAccount, String bankAccountName, String bankShortName,
                                  String phoneAuthenticated, boolean mmsActive, String nationalId,
                                  Long validFeeTo, Long validFrom, Long timeCreate, String phoneNo, String email, boolean status, String vso,
                                  boolean isValidService, boolean isAuthenticated, Integer bankTypeStatus,
                                  String bankCode) {
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.bankAccountName = bankAccountName;
        this.bankShortName = bankShortName;
        this.phoneAuthenticated = phoneAuthenticated;
        this.mmsActive = mmsActive;
        this.nationalId = nationalId;
        this.validFeeTo = validFeeTo;
        this.validFrom = validFrom;
        this.timeCreate = timeCreate;
        this.phoneNo = phoneNo;
        this.email = email;
        this.status = status;
        this.vso = vso;
        this.isValidService = isValidService;
        this.isAuthenticated = isAuthenticated;
        this.bankTypeStatus = bankTypeStatus;
        this.bankCode = bankCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    // Getters and Setters

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public boolean isMmsActive() {
        return mmsActive;
    }

    public void setMmsActive(boolean mmsActive) {
        this.mmsActive = mmsActive;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getVso() {
        return vso;
    }

    public void setVso(String vso) {
        this.vso = vso;
    }

    public Long getValidFeeTo() {
        return validFeeTo;
    }

    public void setValidFeeTo(Long validFeeTo) {
        this.validFeeTo = validFeeTo;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public boolean isValidService() {
        return isValidService;
    }

    public void setValidService(boolean validService) {
        isValidService = validService;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public int getBankTypeStatus() {
        return bankTypeStatus;
    }

    public void setBankTypeStatus(int bankTypeStatus) {
        this.bankTypeStatus = bankTypeStatus;
    }

    public Long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Long validFrom) {
        this.validFrom = validFrom;
    }

    public Long getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Long timeCreate) {
        this.timeCreate = timeCreate;
    }
}
