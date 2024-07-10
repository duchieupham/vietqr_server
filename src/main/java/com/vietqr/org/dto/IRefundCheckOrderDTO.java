package com.vietqr.org.dto;

public interface IRefundCheckOrderDTO {
    Integer getRefundCount();
    Long getAmountRefunded();
    String getTransactionId();
}
