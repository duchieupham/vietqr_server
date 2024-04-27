package com.vietqr.org.dto;

public class InvoiceResponseDTO {
    private String invoiceId;
    private String billNumber;
    private String invoiceNumber;
    private String invoiceName;
    private long timeCreated;
    private long timePaid;
    private int status;
    private String bankId;
    private String bankAccount;
    private String bankShortName;
    private String qrCode;
    private long totalAmount;
    private String bankNameForPayment;
    private String bankAccountForPayment;
    private String bankCodeForPayment;
    private String userBankNameForPayment;

    public InvoiceResponseDTO() {
    }

    public InvoiceResponseDTO(String invoiceId, String billNumber, String invoiceNumber,
                              String invoiceName, long timeCreated, long timePaid, int status,
                              String bankId, String bankAccount, String bankShortName,
                              String qrCode, long totalAmount) {
        this.invoiceId = invoiceId;
        this.billNumber = billNumber;
        this.invoiceNumber = invoiceNumber;
        this.invoiceName = invoiceName;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.status = status;
        this.bankId = bankId;
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.qrCode = qrCode;
        this.totalAmount = totalAmount;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceName() {
        return invoiceName;
    }

    public void setInvoiceName(String invoiceName) {
        this.invoiceName = invoiceName;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(long timePaid) {
        this.timePaid = timePaid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBankNameForPayment() {
        return bankNameForPayment;
    }

    public void setBankNameForPayment(String bankNameForPayment) {
        this.bankNameForPayment = bankNameForPayment;
    }

    public String getBankAccountForPayment() {
        return bankAccountForPayment;
    }

    public void setBankAccountForPayment(String bankAccountForPayment) {
        this.bankAccountForPayment = bankAccountForPayment;
    }

    public String getBankCodeForPayment() {
        return bankCodeForPayment;
    }

    public void setBankCodeForPayment(String bankCodeForPayment) {
        this.bankCodeForPayment = bankCodeForPayment;
    }

    public String getUserBankNameForPayment() {
        return userBankNameForPayment;
    }

    public void setUserBankNameForPayment(String userBankNameForPayment) {
        this.userBankNameForPayment = userBankNameForPayment;
    }
}