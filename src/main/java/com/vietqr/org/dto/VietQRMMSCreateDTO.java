package com.vietqr.org.dto;

import java.io.Serializable;

public class VietQRMMSCreateDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankAccount;
    private String bankCode;
    private String amount;
    private String content;
    private String orderId;
    private String sign;
    private String terminalCode;
    private String note;
    private String urlLink;

    public VietQRMMSCreateDTO() {
        super();
    }

    public VietQRMMSCreateDTO(String bankAccount, String bankCode, String amount, String content, String orderId,
            String sign) {
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.amount = amount;
        this.content = content;
        this.orderId = orderId;
        this.sign = sign;
    }

    public VietQRMMSCreateDTO(String bankAccount, String bankCode, String amount, String content, String orderId,
            String sign, String terminalCode, String note) {
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.amount = amount;
        this.content = content;
        this.orderId = orderId;
        this.sign = sign;
        this.terminalCode = terminalCode;
        this.note = note;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
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
}
