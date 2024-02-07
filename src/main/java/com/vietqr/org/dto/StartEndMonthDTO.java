package com.vietqr.org.dto;

public class StartEndMonthDTO {
    private long fromDate;
    private long toDate;

    public StartEndMonthDTO() {
    }

    public StartEndMonthDTO(long fromDate, long toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public long getFromDate() {
        return fromDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public long getToDate() {
        return toDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }
}
