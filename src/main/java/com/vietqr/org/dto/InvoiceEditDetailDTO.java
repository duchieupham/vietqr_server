package com.vietqr.org.dto;

import java.util.List;

public class InvoiceEditDetailDTO {
    private String invoiceId;
    private String invoiceName;
    private String description;
    private long totalAmount;
    private long vatAmount;
    private long totalAfterVat;
    private double vat;
    private InvoiceDetailCustomerDTO userInformation;
    private long totalUnpaid;
    private long totalPaid;
    List<BankReceivePaymentRequestDTO> paymentRequestDTOS;
    private List<InvoiceItemDetailDTO> invoiceItems;

    public InvoiceEditDetailDTO() {
    }

    public InvoiceEditDetailDTO(String invoiceId, String invoiceName, String description, long totalAmount,
                                long vatAmount, long totalAfterVat, InvoiceDetailCustomerDTO userInformation, List<InvoiceItemDetailDTO> invoiceItems) {
        this.invoiceId = invoiceId;
        this.invoiceName = invoiceName;
        this.description = description;
        this.totalAmount = totalAmount;
        this.vatAmount = vatAmount;
        this.totalAfterVat = totalAfterVat;
        this.userInformation = userInformation;
        this.invoiceItems = invoiceItems;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
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

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(long vatAmount) {
        this.vatAmount = vatAmount;
    }

    public long getTotalAfterVat() {
        return totalAfterVat;
    }

    public void setTotalAfterVat(long totalAfterVat) {
        this.totalAfterVat = totalAfterVat;
    }

    public InvoiceDetailCustomerDTO getUserInformation() {
        return userInformation;
    }

    public void setUserInformation(InvoiceDetailCustomerDTO userInformation) {
        this.userInformation = userInformation;
    }

    public List<InvoiceItemDetailDTO> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItemDetailDTO> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public long getTotalUnpaid() {
        return totalUnpaid;
    }

    public void setTotalUnpaid(long totalUnpaid) {
        this.totalUnpaid = totalUnpaid;
    }

    public long getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(long totalPaid) {
        this.totalPaid = totalPaid;
    }

    public List<BankReceivePaymentRequestDTO> getPaymentRequestDTOS() {
        return paymentRequestDTOS;
    }

    public void setPaymentRequestDTOS(List<BankReceivePaymentRequestDTO> paymentRequestDTOS) {
        this.paymentRequestDTOS = paymentRequestDTOS;
    }
}