package com.vietqr.org.dto;

import java.io.Serializable;

public class TransStatisticMerchantDTOImpl implements TransStatisticMerchantDTO, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String time;
    private Long totalTrans;
    private Long totalAmount;
    private Long totalCredit;
    private Long totalDebit;
    private Long totalTransC;
    private Long totalTransD;

    public TransStatisticMerchantDTOImpl(String time, Long totalTrans, Long totalAmount, Long totalCredit,
            Long totalDebit, Long totalTransC, Long totalTransD) {
        this.time = time;
        this.totalTrans = totalTrans;
        this.totalAmount = totalAmount;
        this.totalCredit = totalCredit;
        this.totalDebit = totalDebit;
        this.totalTransC = totalTransC;
        this.totalTransD = totalTransD;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public Long getTotalTrans() {
        return totalTrans;
    }

    @Override
    public Long getTotalAmount() {
        return totalAmount;
    }

    @Override
    public Long getTotalCredit() {
        return totalCredit;
    }

    @Override
    public Long getTotalDebit() {
        return totalDebit;
    }

    @Override
    public Long getTotalTransC() {
        return totalTransC;
    }

    @Override
    public Long getTotalTransD() {
        return totalTransD;
    }

}
