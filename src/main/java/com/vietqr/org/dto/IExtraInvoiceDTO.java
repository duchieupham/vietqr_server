package com.vietqr.org.dto;

public interface IExtraInvoiceDTO {
    long getCompleteFee();  // Số tiền đã thanh toán
    long getPendingFee();   // Số tiền chưa thanh toán
}
