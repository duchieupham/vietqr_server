package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class RefundRequestDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String bankAccount;

    @NotBlank
    private String referenceNumber;

    @NotBlank
    private String amount;

    @NotBlank
    private String content;

    private String terminalCode;

    private String subTerminalCode;

    private Boolean multiTimes;

    @NotBlank
    private String checkSum;

    public RefundRequestDTO() {
        super();
    }

    public RefundRequestDTO(String bankAccount, String referenceNumber, String amount, String content,
            String checkSum) {
        this.bankAccount = bankAccount;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.content = content;
        this.checkSum = checkSum;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public Boolean getMultiTimes() {
        return multiTimes;
    }

    public void setMultiTimes(Boolean multiTimes) {
        this.multiTimes = multiTimes;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getSubTerminalCode() {
        return subTerminalCode;
    }

    public void setSubTerminalCode(String subTerminalCode) {
        this.subTerminalCode = subTerminalCode;
    }
}
