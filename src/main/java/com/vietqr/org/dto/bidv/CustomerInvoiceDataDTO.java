package com.vietqr.org.dto.bidv;

public interface CustomerInvoiceDataDTO {

    public String getBillId();

    public Long getAmount();

    public int getStatus();

    public int getType();

    public String getName();

    public Long getTimeCreated();

    public Long getTimePaid();

}
