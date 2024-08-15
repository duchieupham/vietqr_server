package com.vietqr.org.dto;

public class BIDVGenerateQrDTO {
    private String bankId;
    private String content;
    private String amount;
    private String billId;

    public BIDVGenerateQrDTO() {
    }

    public BIDVGenerateQrDTO(String bankId, String content, String amount) {
        this.bankId = bankId;
        this.content = content;
        this.amount = amount;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
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

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }
}
