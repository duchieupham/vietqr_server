package com.vietqr.org.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankAccountStatisticDTO {
    @JsonProperty(value = "mmsActive")
    private boolean mmsActive;
    @JsonProperty(value = "bankAccount")
    private String bankAccount;
    @JsonProperty(value = "userBankName")
    private String userBankName;
    @JsonProperty(value = "bankShortName")
    private String bankShortName;
    @JsonProperty(value = "totalCount")
    private Integer totalCount;
    @JsonProperty(value = "totalAmount")
    private Long totalAmount;
    @JsonProperty(value = "creditCount")
    private Integer creditCount;
    @JsonProperty(value = "creditAmount")
    private Long creditAmount;
    @JsonProperty(value = "debitCount")
    private Integer debitCount;
    @JsonProperty(value = "debitAmount")
    private Long debitAmount;
    @JsonProperty(value = "reconCount")
    private Integer reconCount;
    @JsonProperty(value = "reconAmount")
    private Long reconAmount;

    // Getters và setters
    public boolean isMmsActive() {
        return mmsActive;
    }

    public void setMmsActive(boolean mmsActive) {
        this.mmsActive = mmsActive;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getCreditCount() {
        return creditCount;
    }

    public void setCreditCount(Integer creditCount) {
        this.creditCount = creditCount;
    }

    public Long getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Long creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Integer getDebitCount() {
        return debitCount;
    }

    public void setDebitCount(Integer debitCount) {
        this.debitCount = debitCount;
    }

    public Long getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(Long debitAmount) {
        this.debitAmount = debitAmount;
    }

    public Integer getReconCount() {
        return reconCount;
    }

    public void setReconCount(Integer reconCount) {
        this.reconCount = reconCount;
    }

    public Long getReconAmount() {
        return reconAmount;
    }

    public void setReconAmount(Long reconAmount) {
        this.reconAmount = reconAmount;
    }
}
