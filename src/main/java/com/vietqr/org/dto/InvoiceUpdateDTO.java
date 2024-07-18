package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class InvoiceUpdateDTO {
    @NotBlank
    private String bankId;
    private String merchantId;
    private String invoiceName;
    private String description;
    private double vat;
    private String bankIdRecharge;
    private List<InvoiceItemUpdateDTO> items;

    public InvoiceUpdateDTO() {
    }

    public InvoiceUpdateDTO(String bankId, String merchantId, String invoiceName, String description, double vat,
                            String bankIdRecharge, List<InvoiceItemUpdateDTO> items) {
        this.bankId = bankId;
        this.merchantId = merchantId;
        this.invoiceName = invoiceName;
        this.description = description;
        this.vat = vat;
        this.bankIdRecharge = bankIdRecharge;
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

    public String getBankIdRecharge() {
        return bankIdRecharge;
    }

    public void setBankIdRecharge(String bankIdRecharge) {
        this.bankIdRecharge = bankIdRecharge;
    }

    public List<InvoiceItemUpdateDTO> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemUpdateDTO> items) {
        this.items = items;
    }
}
