package com.vietqr.org.dto;

public class TransStatisticListExtra {
    private Long totalCredit;
    private Long totalDebit;

    public TransStatisticListExtra() {
        totalCredit = 0L;
        totalDebit = 0L;
    }

    public TransStatisticListExtra(Long totalCredit, Long totalDebit) {
        this.totalCredit = totalCredit;
        this.totalDebit = totalDebit;
    }

    public Long getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(Long totalCredit) {
        this.totalCredit = totalCredit;
    }

    public Long getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(Long totalDebit) {
        this.totalDebit = totalDebit;
    }
}
