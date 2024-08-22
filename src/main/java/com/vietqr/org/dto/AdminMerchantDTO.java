package com.vietqr.org.dto;

public class AdminMerchantDTO {
    private String invoiceId;
    private String vso;
    private String merchantName;
    private long pendingAmount;
    private long completeAmount;
    private String vietQrAccount;
    private String email;


    public AdminMerchantDTO() {
    }

    public AdminMerchantDTO(String invoiceId, String vso, String merchantName, long pendingAmount, long completeAmount, String vietQrAccount, String email) {
        this.invoiceId = invoiceId;
        this.vso = vso;
        this.merchantName = merchantName;
        this.pendingAmount = pendingAmount;
        this.completeAmount = completeAmount;
        this.vietQrAccount = vietQrAccount;
        this.email = email;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getVso() {
        return vso;
    }

    public void setVso(String vso) {
        this.vso = vso;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }


    public long getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(long pendingAmount) {
        this.pendingAmount = pendingAmount;
    }


    public long getCompleteAmount() {
        return completeAmount;
    }

    public void setCompleteAmount(long completeAmount) {
        this.completeAmount = completeAmount;
    }

    public String getVietQrAccount() {
        return vietQrAccount;
    }

    public void setVietQrAccount(String vietQrAccount) {
        this.vietQrAccount = vietQrAccount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
