package com.vietqr.org.dto;

import java.io.Serializable;

public class TransTCSftpDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String no;
    private String channel;
    private String channelName;
    private String transType;
    private String requestId;
    private String bankTransId;
    private String ft;
    private String datetime;
    private String amount;
    private String currency;
    private String content;
    private String status;
    private String debitAccount;
    private String debitAccountName;
    private String creditAccount;
    private String creditAccountName;
    private String bankAccount;
    private String bankName;
    private String napasKey;
    private String addInfo;
    private String checkSum;

    public TransTCSftpDTO() {
    }

    public TransTCSftpDTO(String no, String channel, String channelName, String transType, String requestId,
            String bankTransId, String ft, String datetime, String amount, String currency, String content,
            String status, String debitAccount, String debitAccountName, String creditAccount, String creditAccountName,
            String bankAccount, String bankName, String napasKey, String addInfo, String checkSum) {
        this.no = no;
        this.channel = channel;
        this.channelName = channelName;
        this.transType = transType;
        this.requestId = requestId;
        this.bankTransId = bankTransId;
        this.ft = ft;
        this.datetime = datetime;
        this.amount = amount;
        this.currency = currency;
        this.content = content;
        this.status = status;
        this.debitAccount = debitAccount;
        this.debitAccountName = debitAccountName;
        this.creditAccount = creditAccount;
        this.creditAccountName = creditAccountName;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
        this.napasKey = napasKey;
        this.addInfo = addInfo;
        this.checkSum = checkSum;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getBankTransId() {
        return bankTransId;
    }

    public void setBankTransId(String bankTransId) {
        this.bankTransId = bankTransId;
    }

    public String getFt() {
        return ft;
    }

    public void setFt(String ft) {
        this.ft = ft;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDebitAccount() {
        return debitAccount;
    }

    public void setDebitAccount(String debitAccount) {
        this.debitAccount = debitAccount;
    }

    public String getDebitAccountName() {
        return debitAccountName;
    }

    public void setDebitAccountName(String debitAccountName) {
        this.debitAccountName = debitAccountName;
    }

    public String getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(String creditAccount) {
        this.creditAccount = creditAccount;
    }

    public String getCreditAccountName() {
        return creditAccountName;
    }

    public void setCreditAccountName(String creditAccountName) {
        this.creditAccountName = creditAccountName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getNapasKey() {
        return napasKey;
    }

    public void setNapasKey(String napasKey) {
        this.napasKey = napasKey;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    @Override
    public String toString() {
        return no + "|" + channel + "|" + channelName + "|" + transType + "|" + requestId + "|" + bankTransId + "|" + ft
                + "|" + datetime + "|" + amount + "|" + currency + "|" + content + "|" + status + "|" + debitAccount
                + "|" + debitAccountName + "|" + creditAccount + "|" + creditAccountName + "|" + bankAccount + "|"
                + bankName + "|" + napasKey + "|" + addInfo + "|" + checkSum;
    }

}
