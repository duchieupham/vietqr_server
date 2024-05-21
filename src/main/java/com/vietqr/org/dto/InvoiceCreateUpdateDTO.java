package com.vietqr.org.dto;

import java.util.List;

public class InvoiceCreateUpdateDTO {
    private String bankId;
    private String merchantId;
    private String invoiceName;
    private String description;
    private double vat;
    private List<InvoiceItemCreateDTO> items;

    public InvoiceCreateUpdateDTO() {
    }

    public InvoiceCreateUpdateDTO(String bankId, String merchantId, List<InvoiceItemCreateDTO> items) {
        this.bankId = bankId;
        this.merchantId = merchantId;
        this.items = items;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public List<InvoiceItemCreateDTO> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemCreateDTO> items) {
        this.items = items;
    }

    public String getInvoiceName() {
        return invoiceName;
    }

    public void setInvoiceName(String invoiceName) {
        this.invoiceName = invoiceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }
}
