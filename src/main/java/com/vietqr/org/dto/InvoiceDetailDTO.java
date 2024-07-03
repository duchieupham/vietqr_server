package com.vietqr.org.dto;

import java.util.List;

public class InvoiceDetailDTO {
    private String invoiceId;
    private String billNumber;
    private String invoiceNumber;
    private String invoiceName;
    private long timeCreated;
    private long timePaid;
    private int status;
    private long vatAmount;
    private long amount;
    private double vat;
    private String bankId;
    private String bankShortName;
    private String bankAccount;
    private String bankNameForPayment;
    private String bankAccountForPayment;
    private String bankCodeForPayment;
    private String userBankNameForPayment;
    private String fileAttachmentId;
    private String qrCode;
    private long totalAmount;
    private List<InvoiceItemResDTO> items;

    public InvoiceDetailDTO() {
    }

    public InvoiceDetailDTO(String invoiceId, String billNumber, String invoiceNumber, String invoiceName, long timeCreated, long timePaid, int status, long vatAmount, long amount, double vat, String bankId, String bankShortName, String bankAccount, String bankNameForPayment, String bankAccountForPayment, String bankCodeForPayment, String userBankNameForPayment, String fileAttachmentId, String qrCode, long totalAmount, List<InvoiceItemResDTO> items) {
        this.invoiceId = invoiceId;
        this.billNumber = billNumber;
        this.invoiceNumber = invoiceNumber;
        this.invoiceName = invoiceName;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.status = status;
        this.vatAmount = vatAmount;
        this.amount = amount;
        this.vat = vat;
        this.bankId = bankId;
        this.bankShortName = bankShortName;
        this.bankAccount = bankAccount;
        this.bankNameForPayment = bankNameForPayment;
        this.bankAccountForPayment = bankAccountForPayment;
        this.bankCodeForPayment = bankCodeForPayment;
        this.userBankNameForPayment = userBankNameForPayment;
        this.fileAttachmentId = fileAttachmentId;
        this.qrCode = qrCode;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public String getFileAttachmentId() {
        return fileAttachmentId;
    }

    public void setFileAttachmentId(String fileAttachmentId) {
        this.fileAttachmentId = fileAttachmentId;
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

    public long getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(long vatAmount) {
        this.vatAmount = vatAmount;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
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

    public List<InvoiceItemResDTO> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemResDTO> items) {
        this.items = items;
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
