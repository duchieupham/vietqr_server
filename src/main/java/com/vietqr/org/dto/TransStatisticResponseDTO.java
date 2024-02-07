package com.vietqr.org.dto;

public class TransStatisticResponseDTO {
    private Long totalTrans;

    private Long totalCashIn;

    private Long totalCashOut;

    private Long totalTransC;

    private Long totalTransD;

    public TransStatisticResponseDTO() {
    }

    public TransStatisticResponseDTO(Long totalTrans, Long totalCashIn, Long totalCashOut, Long totalTransC, Long totalTransD) {
        this.totalTrans = totalTrans;
        this.totalCashIn = totalCashIn;
        this.totalCashOut = totalCashOut;
        this.totalTransC = totalTransC;
        this.totalTransD = totalTransD;
    }

    public Long getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(Long totalTrans) {
        this.totalTrans = totalTrans;
    }

    public Long getTotalCashIn() {
        return totalCashIn;
    }

    public void setTotalCashIn(Long totalCashIn) {
        this.totalCashIn = totalCashIn;
    }

    public Long getTotalCashOut() {
        return totalCashOut;
    }

    public void setTotalCashOut(Long totalCashOut) {
        this.totalCashOut = totalCashOut;
    }

    public Long getTotalTransC() {
        return totalTransC;
    }

    public void setTotalTransC(Long totalTransC) {
        this.totalTransC = totalTransC;
    }

    public Long getTotalTransD() {
        return totalTransD;
    }

    public void setTotalTransD(Long totalTransD) {
        this.totalTransD = totalTransD;
    }
}
