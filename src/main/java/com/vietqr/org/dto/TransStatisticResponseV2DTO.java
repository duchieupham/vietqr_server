package com.vietqr.org.dto;

import java.util.List;

public class TransStatisticResponseV2DTO {
    private long totalCredit;
    private int countCredit;
    private long totalDebit;
    private int countDebit;

    public TransStatisticResponseV2DTO() {
    }

    public TransStatisticResponseV2DTO(long totalCredit, int countCredit, long totalDebit, int countDebit) {
        this.totalCredit = totalCredit;
        this.countCredit = countCredit;
        this.totalDebit = totalDebit;
        this.countDebit = countDebit;
    }

    public long getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(long totalCredit) {
        this.totalCredit = totalCredit;
    }

    public int getCountCredit() {
        return countCredit;
    }

    public void setCountCredit(int countCredit) {
        this.countCredit = countCredit;
    }

    public long getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(long totalDebit) {
        this.totalDebit = totalDebit;
    }

    public int getCountDebit() {
        return countDebit;
    }

    public void setCountDebit(int countDebit) {
        this.countDebit = countDebit;
    }
}
