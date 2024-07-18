package com.vietqr.org.dto;

public class CheckOrderTransTypeDDTO {
    private String referenceNumber;
    private String orderId;
    private Long amount;
    private String content;
    private String transType;
    private Integer status;
    private Integer type;
    private Long timeCreated;
    private Long timePaid;
    private String terminalCode;
    private String note;

    public CheckOrderTransTypeDDTO() {
    }

    public CheckOrderTransTypeDDTO(String referenceNumber, String orderId, Long amount, String content, String transType,
                                   Integer status, Integer type, Long timeCreated, Long timePaid, String terminalCode,
                                   String note) {
        this.referenceNumber = referenceNumber;
        this.orderId = orderId;
        this.amount = amount;
        this.content = content;
        this.transType = transType;
        this.status = status;
        this.type = type;
        this.timeCreated = timeCreated;
        this.timePaid = timePaid;
        this.terminalCode = terminalCode;
        this.note = note;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Long getTimePaid() {
        return timePaid;
    }

    public void setTimePaid(Long timePaid) {
        this.timePaid = timePaid;
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
}
