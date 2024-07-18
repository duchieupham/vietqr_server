package com.vietqr.org.dto;

public interface IInvoiceResponseDTO {
    String getInvoiceId();
    String getBillNumber();
    String getContent();
    String getInvoiceName();
    String getInvoiceNumber();
    long getTimeCreated();
    int getStatus();
    long getTimePaid();

    String getBankId();
    String getData();
    String getQrCode();
    String getFileAttachmentId();
    long getTotalAmount();
}
