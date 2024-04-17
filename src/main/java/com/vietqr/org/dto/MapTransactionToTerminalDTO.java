package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;

public class MapTransactionToTerminalDTO {
    @NotBlank
    private String transactionId;
    @NotBlank
    private String terminalCode;
    @NotBlank
    private String userId;

    public MapTransactionToTerminalDTO() {
    }

    public MapTransactionToTerminalDTO(String transactionId, String terminalCode, String userId) {
        this.transactionId = transactionId;
        this.terminalCode = terminalCode;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
