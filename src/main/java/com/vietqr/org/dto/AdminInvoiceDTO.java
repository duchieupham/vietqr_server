package com.vietqr.org.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminInvoiceDTO {
    private String invoiceId;
    private long timePaid;
    private String vso;
    private String midName;
    private long amount;
    private String bankShortName;
    private String bankAccount;
    private String qrCode;
    private double vat;
    private long vatAmount;
    private long amountNoVat;
    private String billNumber;
    private String invoiceName;
    private String fullName;
    private String phoneNo;
    private String email;
    private long timeCreated;
    private int status;

    public AdminInvoiceDTO() {
    }

    public AdminInvoiceDTO(String invoiceId, long timePaid, String vso, String midName, long amount,
                           String bankShortName, String bankAccount, String qrCode, double vat,
                           long vatAmount, long amountNoVat, String billNumber, String invoiceName,
                           String fullName, String phoneNo, String email, long timeCreated, int status) {
        this.invoiceId = invoiceId;
        this.timePaid = timePaid;
        this.vso = vso;
        this.midName = midName;
        this.amount = amount;
        this.bankShortName = bankShortName;
        this.bankAccount = bankAccount;
        this.qrCode = qrCode;
        this.vat = vat;
        this.vatAmount = vatAmount;
        this.amountNoVat = amountNoVat;
        this.billNumber = billNumber;
        this.invoiceName = invoiceName;
        this.fullName = fullName;
        this.phoneNo = phoneNo;
        this.email = email;
        this.timeCreated = timeCreated;
        this.status = status;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(long timePaid) {
        this.timePaid = timePaid;
    }

    public String getVso() {
        return vso;
    }

    public void setVso(String vso) {
        this.vso = vso;
    }

    public String getMidName() {
        return midName;
    }

    public void setMidName(String midName) {
        this.midName = midName;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
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

    public long getAmountNoVat() {
        return amountNoVat;
    }

    public void setAmountNoVat(long amountNoVat) {
        this.amountNoVat = amountNoVat;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getInvoiceName() {
        return invoiceName;
    }

    public void setInvoiceName(String invoiceName) {
        this.invoiceName = invoiceName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
