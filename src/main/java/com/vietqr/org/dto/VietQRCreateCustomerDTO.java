package com.vietqr.org.dto;

import java.io.Serializable;

public class VietQRCreateCustomerDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long amount;
    private String content;
    private String bankAccount;
    private String bankCode;
    private String userBankName;
    private String transType;
    private String customerBankAccount;
    private String customerBankCode;
    private String customerName;
    private String orderId;
    private String sign;
    private String terminalCode;
    private String note;
    private String urlLink;
    private Boolean reconciliation;
    private Integer qrType;
    private String serviceCode;

    public VietQRCreateCustomerDTO() {
        super();
    }

    public VietQRCreateCustomerDTO(Long amount, String content, String bankAccount, String bankCode,
            String userBankName, String orderId, String sign) {
        this.amount = amount;
        this.content = content;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.userBankName = userBankName;
        this.orderId = orderId;
        this.sign = sign;
    }

    public VietQRCreateCustomerDTO(Long amount, String content, String bankAccount, String bankCode,
            String userBankName, String orderId, String sign, String terminalCode, String note) {
        this.amount = amount;
        this.content = content;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.userBankName = userBankName;
        this.orderId = orderId;
        this.sign = sign;
        this.terminalCode = terminalCode;
        this.note = note;
    }

    public VietQRCreateCustomerDTO(String bankAccount, Long amount, String content,
            String bankCode, String userBankName, int existing, String transType,
            String customerBankAccount, String customerBankCode, String customerName,
            String orderId, String sign) {
        this.amount = amount;
        this.content = content;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.userBankName = userBankName;
        this.transType = transType;
        this.customerBankAccount = customerBankAccount;
        this.customerBankCode = customerBankCode;
        this.customerName = customerName;
        this.orderId = orderId;
        this.sign = sign;
    }

    public VietQRCreateCustomerDTO(String bankAccount, Long amount, String content,
            String bankCode, String userBankName, int existing, String transType,
            String customerBankAccount, String customerBankCode, String customerName,
            String orderId, String sign, String terminalCode, String note) {
        this.amount = amount;
        this.content = content;
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.userBankName = userBankName;
        this.transType = transType;
        this.customerBankAccount = customerBankAccount;
        this.customerBankCode = customerBankCode;
        this.customerName = customerName;
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

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
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

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
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

    public Boolean getReconciliation() {
        return reconciliation;
    }

    public void setReconciliation(Boolean reconciliation) {
        this.reconciliation = reconciliation;
    }

    public Integer getQrType() {
        return qrType;
    }

    public void setQrType(Integer qrType) {
        this.qrType = qrType;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @Override
    public String toString() {
        return "VietQRCreateCustomerDTO [amount=" + amount + ", content=" + content + ", bankAccount=" + bankAccount
                + ", bankCode=" + bankCode + ", userBankName=" + userBankName + ", transType=" + transType
                + ", customerBankAccount=" + customerBankAccount + ", customerBankCode=" + customerBankCode
                + ", customerName=" + customerName + ", orderId=" + orderId + ", sign=" + sign
                + ", urlLink=" + urlLink + ", serviceCode=" + serviceCode + ", qrType=" + qrType + "]";
    }

}
