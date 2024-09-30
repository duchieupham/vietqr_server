package com.vietqr.org.dto;

import com.vietqr.org.service.mqtt.AdditionalData;

import java.io.Serializable;
import java.util.List;

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
    private String serviceCode = "";
    private String subTerminalCode = "";
    private List<AdditionalData> additionalData;

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

    public VietQRMMSCreateDTO(String bankAccount, String bankCode, String amount, String content, String orderId, String sign, String terminalCode, String note, String urlLink, String serviceCode, String subTerminalCode, List<AdditionalData> additionalData) {
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.amount = amount;
        this.content = content;
        this.orderId = orderId;
        this.sign = sign;
        this.terminalCode = terminalCode;
        this.note = note;
        this.urlLink = urlLink;
        this.serviceCode = serviceCode;
        this.subTerminalCode = subTerminalCode;
        this.additionalData = additionalData;
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

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getSubTerminalCode() {
        return subTerminalCode;
    }

    public void setSubTerminalCode(String subTerminalCode) {
        this.subTerminalCode = subTerminalCode;
    }

    public List<AdditionalData> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(List<AdditionalData> additionalData) {
        this.additionalData = additionalData;
    }
}
