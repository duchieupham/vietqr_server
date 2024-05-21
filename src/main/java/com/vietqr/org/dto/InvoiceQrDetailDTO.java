package com.vietqr.org.dto;

public class InvoiceQrDetailDTO {
    private String qrCode;
    private long totalAmountAfterVat;
    private String invoiceName;
    private String bankAccount;
    private String bankShortName;
    private String invoiceNumber;
    private String userBankName;
    private long totalAmount;
    private double vat;
    private long vatAmount;
    private String invoiceId;

    public InvoiceQrDetailDTO() {
        qrCode = "";
        totalAmountAfterVat = 0;
        invoiceName = "";
        bankAccount = "";
        bankShortName = "";
        invoiceNumber = "";
        userBankName = "";
        totalAmount = 0;
        vat = 0;
        vatAmount = 0;
        invoiceId = "";
    }

    public InvoiceQrDetailDTO(String qrCode, long totalAmountAfterVat, String invoiceName, String bankAccount,
                              String bankShortName, String invoiceNumber, String userBankName, long totalAmount,
                              double vat, long vatAmount, String invoiceId) {
        this.qrCode = qrCode;
        this.totalAmountAfterVat = totalAmountAfterVat;
        this.invoiceName = invoiceName;
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.invoiceNumber = invoiceNumber;
        this.userBankName = userBankName;
        this.totalAmount = totalAmount;
        this.vat = vat;
        this.vatAmount = vatAmount;
        this.invoiceId = invoiceId;
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
}
