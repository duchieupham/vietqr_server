package com.vietqr.org.dto;

import com.vietqr.org.entity.BankTypeEntity;
import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.util.DateTimeUtil;

public class NotificationFcmMapDTO {
    private String notificationUUID;
    private String transId;
    private String bankAccount;
    private String bankName;
    private String bankCode;
    private String bankId;
    private String terminalName;
    private String terminalCode;
    private String rawTerminalCode;
    private String orderId;
    private String referenceNumber;
    private String content;
    private String amount;
    private String timePaid;
    private String type;
    private String time;
    private String refId;
    private String traceId;
    private String transType;
    private String urlLink;

    public NotificationFcmMapDTO() {
    }

    public NotificationFcmMapDTO(String notificationUUID, BankTypeEntity bankTypeEntity,
                                 String terminalName, String terminalCode, String rawTerminalCode, String amount,
                                 TransactionReceiveEntity transactionReceiveEntity) {
        this.notificationUUID = notificationUUID;
        this.transId = transactionReceiveEntity.getId();
        this.bankAccount = transactionReceiveEntity.getBankAccount();
        this.bankName = bankTypeEntity.getBankName();
        this.bankCode = bankTypeEntity.getBankCode();
        this.bankId = transactionReceiveEntity.getBankId();
        this.terminalName = terminalName;
        this.terminalCode = terminalCode;
        this.rawTerminalCode = rawTerminalCode;
        this.orderId = transactionReceiveEntity.getOrderId();
        this.referenceNumber = transactionReceiveEntity.getReferenceNumber();
        this.content = transactionReceiveEntity.getContent();
        this.amount = amount;
        this.timePaid = DateTimeUtil.getCurrentDateTimeUTC() + "";
        this.type = transactionReceiveEntity.getType() + "";
        this.time = transactionReceiveEntity.getTime() + "";
        this.refId = transactionReceiveEntity.getRefId();
        this.traceId = transactionReceiveEntity.getTraceId();
        this.transType = transactionReceiveEntity.getTransType();
        this.urlLink = transactionReceiveEntity.getUrlLink();
    }

    public String getNotificationUUID() {
        return notificationUUID;
    }

    public void setNotificationUUID(String notificationUUID) {
        this.notificationUUID = notificationUUID;
    }

    public String getTransId() {
        return transId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getRawTerminalCode() {
        return rawTerminalCode;
    }

    public void setRawTerminalCode(String rawTerminalCode) {
        this.rawTerminalCode = rawTerminalCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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

    public String getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(String timePaid) {
        this.timePaid = timePaid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }
}
