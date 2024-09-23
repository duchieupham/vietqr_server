package com.vietqr.org.dto;

public class PaymentRequestResponseDTO {
    private String qrCode;
    private long totalAmountAfterVat;
    private String invoiceName;
    private String midName;
    private String vso;
    private String bankAccount;
    private String bankShortName;
    private String invoiceNumber;
    private String userBankName;
    private long totalAmount;
    private double vat;
    private long vatAmount;
    private String invoiceId;
    private long expiredTime;
    private String bankCode;
    private String urlLink;

    public PaymentRequestResponseDTO() {
    }

    public PaymentRequestResponseDTO(String qrCode, long totalAmountAfterVat, String invoiceName, String midName,
                                     String vso, String bankAccount, String bankShortName, String invoiceNumber,
                                     String userBankName, long totalAmount, double vat, long vatAmount, String invoiceId, String bankCode, String urlLink) {
        this.qrCode = qrCode;
        this.totalAmountAfterVat = totalAmountAfterVat;
        this.invoiceName = invoiceName;
        this.midName = midName;
        this.vso = vso;
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.invoiceNumber = invoiceNumber;
        this.userBankName = userBankName;
        this.totalAmount = totalAmount;
        this.vat = vat;
        this.vatAmount = vatAmount;
        this.invoiceId = invoiceId;
        this.bankCode = bankCode;
        this.urlLink = urlLink;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public long getTotalAmountAfterVat() {
        return totalAmountAfterVat;
    }

    public void setTotalAmountAfterVat(long totalAmountAfterVat) {
        this.totalAmountAfterVat = totalAmountAfterVat;
    }

    public String getInvoiceName() {
        return invoiceName;
    }

    public void setInvoiceName(String invoiceName) {
        this.invoiceName = invoiceName;
    }

    public String getMidName() {
        return midName;
    }

    public void setMidName(String midName) {
        this.midName = midName;
    }

    public String getVso() {
        return vso;
    }

    public void setVso(String vso) {
        this.vso = vso;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public long getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(long vatAmount) {
        this.vatAmount = vatAmount;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }


    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }
}
