package com.vietqr.org.dto;

import java.util.List;

public class InvoiceDetailAdminDTO {
    private List<CustomerDetailDTO> customerDetailDTOS;
    private List<FeePackageDetailDTO> feePackageDetailDTOS;
    private  List<InvoiceItemDetailDTO> invoiceItemDetailDTOS;
    private List<BankReceivePaymentRequestDTO> paymentRequestDTOS;
    private String invoiceId;
    private String invoiceName;
    private String invoiceDescription;
    private double vat;
    private long vatAmount;
    private long totalAmount;
    private long totalAmountAfterVat;
    private int status;

    private long totalPaid;
    private long totalUnpaid;

    public long getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(long totalPaid) {
        this.totalPaid = totalPaid;
    }

    public long getTotalUnpaid() {
        return totalUnpaid;
    }

    public void setTotalUnpaid(long totalUnpaid) {
        this.totalUnpaid = totalUnpaid;
    }

    public InvoiceDetailAdminDTO() {
    }

    public InvoiceDetailAdminDTO(List<CustomerDetailDTO> customerDetailDTOS, List<FeePackageDetailDTO> feePackageDetailDTOS,
                                 List<InvoiceItemDetailDTO> invoiceItemDetailDTOS, String invoiceId, String invoiceName,
                                 String invoiceDescription, double vat, long vatAmount, long totalAmount,
                                 long totalAmountAfterVat, int status) {
        this.customerDetailDTOS = customerDetailDTOS;
        this.feePackageDetailDTOS = feePackageDetailDTOS;
        this.invoiceItemDetailDTOS = invoiceItemDetailDTOS;
        this.invoiceId = invoiceId;
        this.invoiceName = invoiceName;
        this.invoiceDescription = invoiceDescription;
        this.vat = vat;
        this.vatAmount = vatAmount;
        this.totalAmount = totalAmount;
        this.totalAmountAfterVat = totalAmountAfterVat;
        this.status = status;
    }

    public List<CustomerDetailDTO> getCustomerDetailDTOS() {
        return customerDetailDTOS;
    }

    public void setCustomerDetailDTOS(List<CustomerDetailDTO> customerDetailDTOS) {
        this.customerDetailDTOS = customerDetailDTOS;
    }

    public List<FeePackageDetailDTO> getFeePackageDetailDTOS() {
        return feePackageDetailDTOS;
    }

    public void setFeePackageDetailDTOS(List<FeePackageDetailDTO> feePackageDetailDTOS) {
        this.feePackageDetailDTOS = feePackageDetailDTOS;
    }

    public List<InvoiceItemDetailDTO> getInvoiceItemDetailDTOS() {
        return invoiceItemDetailDTOS;
    }

    public void setInvoiceItemDetailDTOS(List<InvoiceItemDetailDTO> invoiceItemDetailDTOS) {
        this.invoiceItemDetailDTOS = invoiceItemDetailDTOS;
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

    public String getInvoiceDescription() {
        return invoiceDescription;
    }

    public void setInvoiceDescription(String invoiceDescription) {
        this.invoiceDescription = invoiceDescription;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public long getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(long vatAmount) {
        this.vatAmount = vatAmount;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getTotalAmountAfterVat() {
        return totalAmountAfterVat;
    }

    public void setTotalAmountAfterVat(long totalAmountAfterVat) {
        this.totalAmountAfterVat = totalAmountAfterVat;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<BankReceivePaymentRequestDTO> getPaymentRequestDTOS() {
        return paymentRequestDTOS;
    }

    public void setPaymentRequestDTOS(List<BankReceivePaymentRequestDTO> paymentRequestDTOS) {
        this.paymentRequestDTOS = paymentRequestDTOS;
    }
}
