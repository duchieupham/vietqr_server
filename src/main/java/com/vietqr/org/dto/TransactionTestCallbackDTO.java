package com.vietqr.org.dto;

public class TransactionTestCallbackDTO {
    private String bankAccount;
    private String content;
    private String amount;
    private String transType;

    public TransactionTestCallbackDTO() {
        super();
    }

    public TransactionTestCallbackDTO(String bankAccount, String content, String amount, String transType) {
        this.bankAccount = bankAccount;
        this.content = content;
        this.amount = amount;
        this.transType = transType;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
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

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

}
