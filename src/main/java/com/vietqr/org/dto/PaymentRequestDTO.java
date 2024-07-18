package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class PaymentRequestDTO {
    @NotBlank
    private String invoiceId;
    private List<String> itemItemIds;
    private String bankIdRecharge;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(String invoiceId, List<String> itemItemIds, String bankIdRecharge) {
        this.invoiceId = invoiceId;
        this.itemItemIds = itemItemIds;
        this.bankIdRecharge = bankIdRecharge;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public List<String> getItemItemIds() {
        return itemItemIds;
    }

    public void setItemItemIds(List<String> itemItemIds) {
        this.itemItemIds = itemItemIds;
    }

    public String getBankIdRecharge() {
        return bankIdRecharge;
    }

    public void setBankIdRecharge(String bankIdRecharge) {
        this.bankIdRecharge = bankIdRecharge;
    }
}
