package com.vietqr.org.dto;

import java.io.Serializable;

public class TransFeeMerchantDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankAccount;
    private String bankCode;
    private String bankShortName;
    private String serviceName;
    private Long totalTrans;
    private Long totalAmount;
    private Long totalFee;
    private Double vat;
    private Long vatFee;
    private Long annualFee;
    private Long totalFeeAfterVat;
    private Integer status;

    public TransFeeMerchantDTO() {
        super();
    }

    public TransFeeMerchantDTO(String bankAccount, String bankCode, String bankShortName, String serviceName,
            Long totalTrans, Long totalAmount, Long totalFee, Double vat, Long vatFee, Long annualFee,
            Long totalFeeAfterVat,
            Integer status) {
        this.bankAccount = bankAccount;
        this.bankCode = bankCode;
        this.bankShortName = bankShortName;
        this.serviceName = serviceName;
        this.totalTrans = totalTrans;
        this.totalAmount = totalAmount;
        this.totalFee = totalFee;
        this.vat = vat;
        this.vatFee = vatFee;
        this.annualFee = annualFee;
        this.totalFeeAfterVat = totalFeeAfterVat;
        this.status = status;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(Long totalTrans) {
        this.totalTrans = totalTrans;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public Long getVatFee() {
        return vatFee;
    }

    public void setVatFee(Long vatFee) {
        this.vatFee = vatFee;
    }

    public Long getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(Long annualFee) {
        this.annualFee = annualFee;
    }

    public Long getTotalFeeAfterVat() {
        return totalFeeAfterVat;
    }

    public void setTotalFeeAfterVat(Long totalFeeAfterVat) {
        this.totalFeeAfterVat = totalFeeAfterVat;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
