package com.vietqr.org.dto;

import java.util.List;

public class InvoiceRequestDTO {
    private String merchantId;
    private String bankId;
    private String name;
    private String time;
    private List<InvoiceItemDTO> items;

    public InvoiceRequestDTO() {
    }

    public InvoiceRequestDTO(String merchantId, List<InvoiceItemDTO> items) {
        this.merchantId = merchantId;
        this.items = items;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public List<InvoiceItemDTO> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemDTO> items) {
        this.items = items;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
