package com.vietqr.org.dto;

public class RefundCheckOrderDTO {
    private String transactionId;
    private Integer refundCount;
    private Long amountRefunded;

    public RefundCheckOrderDTO() {
    }

    public RefundCheckOrderDTO(String transactionId, Integer refundCount, Long amountRefunded) {
        this.transactionId = transactionId;
        this.refundCount = refundCount;
        this.amountRefunded = amountRefunded;
    }

    public RefundCheckOrderDTO(String transactionId) {
        this.transactionId = transactionId;
        this.refundCount = 0;
        this.amountRefunded = 0L;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getRefundCount() {
        return refundCount;
    }

    public void setRefundCount(Integer refundCount) {
        this.refundCount = refundCount;
    }

    public Long getAmountRefunded() {
        return amountRefunded;
    }

    public void setAmountRefunded(Long amountRefunded) {
        this.amountRefunded = amountRefunded;
    }
}
