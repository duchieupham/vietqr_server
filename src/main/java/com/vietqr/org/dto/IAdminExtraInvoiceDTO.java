package com.vietqr.org.dto;

public interface IAdminExtraInvoiceDTO {
    long getPendingFee();
    int getPendingCount();
    long getCompleteFee();
    int getCompleteCount();
}
