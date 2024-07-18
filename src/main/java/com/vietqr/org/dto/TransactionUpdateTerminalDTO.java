package com.vietqr.org.dto;

public class TransactionUpdateTerminalDTO {
    private String transactionId;
    private String terminalCode;
    private int type;

    private int transStatus;

    public TransactionUpdateTerminalDTO(String transactionId, String terminalCode, int type) {
        this.transactionId = transactionId;
        this.terminalCode = terminalCode;
        this.type = type;
    }

    public int getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(int transStatus) {
        this.transStatus = transStatus;
    }

    public TransactionUpdateTerminalDTO() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
