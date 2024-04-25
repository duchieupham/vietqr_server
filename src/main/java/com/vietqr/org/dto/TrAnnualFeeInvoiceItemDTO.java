package com.vietqr.org.dto;

public class TrAnnualFeeInvoiceItemDTO {
    private String userId;
    private String bankId;
    private String bankAccount;
    private String userBankName;
    private String bankShortName;
    private long amount;
    private long amountAfterVat;
    private long amountVat;
    private long validFrom;
    private long validTo;
    private String transWalletId;

    public TrAnnualFeeInvoiceItemDTO() {
    }

    public TrAnnualFeeInvoiceItemDTO(String userId, String bankId, String bankAccount, String userBankName) {
        this.userId = userId;
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
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

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getAmountAfterVat() {
        return amountAfterVat;
    }

    public void setAmountAfterVat(long amountAfterVat) {
        this.amountAfterVat = amountAfterVat;
    }

    public long getAmountVat() {
        return amountVat;
    }

    public void setAmountVat(long amountVat) {
        this.amountVat = amountVat;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(long validFrom) {
        this.validFrom = validFrom;
    }

    public long getValidTo() {
        return validTo;
    }

    public void setValidTo(long validTo) {
        this.validTo = validTo;
    }

    public String getTransWalletId() {
        return transWalletId;
    }

    public void setTransWalletId(String transWalletId) {
        this.transWalletId = transWalletId;
    }
}
