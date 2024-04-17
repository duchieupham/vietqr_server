package com.vietqr.org.dto;

public class StartEndTimeDTO {
    private long startTime;
    private long endTime;

    public StartEndTimeDTO() {
    }

    public StartEndTimeDTO(long startTime, long toDate) {
        this.startTime = startTime;
        this.endTime = toDate;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
