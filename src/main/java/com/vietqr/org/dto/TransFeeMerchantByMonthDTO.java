package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class TransFeeMerchantByMonthDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String month;
    private String merchantName;
    private List<TransFeeMerchantDTO> fees;
    private Long totalTrans;
    private Long totalAmount;
    private Long totalFee;
    private Long totalVatFee;
    private Long totalFeeAfterVat;
    private Integer status;

    public TransFeeMerchantByMonthDTO() {
        super();
    }

    public TransFeeMerchantByMonthDTO(String month, String merchantName, List<TransFeeMerchantDTO> fees,
            Long totalTrans, Long totalAmount, Long totalFee, Long totalVatFee, Long totalFeeAfterVat, Integer status) {
        this.month = month;
        this.merchantName = merchantName;
        this.fees = fees;
        this.totalTrans = totalTrans;
        this.totalAmount = totalAmount;
        this.totalFee = totalFee;
        this.totalVatFee = totalVatFee;
        this.totalFeeAfterVat = totalFeeAfterVat;
        this.status = status;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public List<TransFeeMerchantDTO> getFees() {
        return fees;
    }

    public void setFees(List<TransFeeMerchantDTO> fees) {
        this.fees = fees;
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

    public Long getTotalVatFee() {
        return totalVatFee;
    }

    public void setTotalVatFee(Long totalVatFee) {
        this.totalVatFee = totalVatFee;
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
