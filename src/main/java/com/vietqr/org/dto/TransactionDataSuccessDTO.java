package com.vietqr.org.dto;

public class TransactionDataSuccessDTO {
    private String transactionReceiveId;
    private String boxId;
    private String boxCode;
    private String terminalCode;
    private String bankAccount;
    private String amount;

    public TransactionDataSuccessDTO() {
    }

    public TransactionDataSuccessDTO(String transactionReceiveId, String boxId) {
        this.transactionReceiveId = transactionReceiveId;
        this.boxId = boxId;
    }

    public String getTransactionReceiveId() {
        return transactionReceiveId;
    }

    public void setTransactionReceiveId(String transactionReceiveId) {
        this.transactionReceiveId = transactionReceiveId;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
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
}
