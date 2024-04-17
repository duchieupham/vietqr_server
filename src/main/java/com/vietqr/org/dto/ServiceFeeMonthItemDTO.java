package com.vietqr.org.dto;

import java.io.Serializable;

public class ServiceFeeMonthItemDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String accountBankFeeId;
    private String serviceFeeId;
    private String shortName;
    private Long totalTrans;
    private Long totalAmount;
    private Double vat;
    private Long totalPayment;
    private Integer countingTransType;
    private Long discountAmount;
    private Integer status;

    public ServiceFeeMonthItemDTO() {
        super();
    }

    public ServiceFeeMonthItemDTO(String accountBankFeeId, String serviceFeeId, String shortName, Long totalTrans,
            Long totalAmount, Double vat, Long totalPayment, Integer countingTransType, Long discountAmount,
            Integer status) {
        this.accountBankFeeId = accountBankFeeId;
        this.serviceFeeId = serviceFeeId;
        this.shortName = shortName;
        this.totalTrans = totalTrans;
        this.totalAmount = totalAmount;
        this.vat = vat;
        this.totalPayment = totalPayment;
        this.countingTransType = countingTransType;
        this.discountAmount = discountAmount;
        this.status = status;
    }

    public String getAccountBankFeeId() {
        return accountBankFeeId;
    }

    public void setAccountBankFeeId(String accountBankFeeId) {
        this.accountBankFeeId = accountBankFeeId;
    }

    public String getServiceFeeId() {
        return serviceFeeId;
    }

    public void setServiceFeeId(String serviceFeeId) {
        this.serviceFeeId = serviceFeeId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
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

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public Long getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Long totalPayment) {
        this.totalPayment = totalPayment;
    }

    public Integer getCountingTransType() {
        return countingTransType;
    }

    public void setCountingTransType(Integer countingTransType) {
        this.countingTransType = countingTransType;
    }

    public Long getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Long discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
