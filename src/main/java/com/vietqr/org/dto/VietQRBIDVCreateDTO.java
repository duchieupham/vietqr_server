package com.vietqr.org.dto;

public class VietQRBIDVCreateDTO {
    private String content;
    private String amount;
    private String terminalCode;
    private String orderId;
    private String note;
    private String urlLink;
    private String transType;
    private String sign;
    private String billId;
    private String customerBankAccount;
    private String customerBankCode;
    private String customerName;
    private String qr;

    public VietQRBIDVCreateDTO() {
    }

    public VietQRBIDVCreateDTO(String content, String amount, String terminalCode,
                               String orderId, String note, String urlLink, String transType,
                               String sign, String billId, String customerBankAccount,
                               String customerBankCode, String customerName) {
        this.content = content;
        this.amount = amount;
        this.terminalCode = terminalCode;
        this.orderId = orderId;
        this.note = note;
        this.urlLink = urlLink;
        this.transType = transType;
        this.sign = sign;
        this.billId = billId;
        this.customerBankAccount = customerBankAccount;
        this.customerBankCode = customerBankCode;
        this.customerName = customerName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getCustomerBankAccount() {
        return customerBankAccount;
    }

    public void setCustomerBankAccount(String customerBankAccount) {
        this.customerBankAccount = customerBankAccount;
    }

    public String getCustomerBankCode() {
        return customerBankCode;
    }

    public void setCustomerBankCode(String customerBankCode) {
        this.customerBankCode = customerBankCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }
}
