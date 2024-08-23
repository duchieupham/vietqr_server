package com.vietqr.org.dto;

public class AdminExtraBankDTO {
    private long overdueCount;
    private long nearlyExpireCount;
    private long validCount;
    private long notRegisteredCount;

    public AdminExtraBankDTO() {
    }

    public AdminExtraBankDTO(long overdueCount, long nearlyExpireCount, long validCount, long notRegisteredCount) {
        this.overdueCount = overdueCount;
        this.nearlyExpireCount = nearlyExpireCount;
        this.validCount = validCount;
        this.notRegisteredCount = notRegisteredCount;
    }

    public long getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(long overdueCount) {
        this.overdueCount = overdueCount;
    }

    public long getNearlyExpireCount() {
        return nearlyExpireCount;
    }

    public void setNearlyExpireCount(long nearlyExpireCount) {
        this.nearlyExpireCount = nearlyExpireCount;
    }

    public long getValidCount() {
        return validCount;
    }

    public void setValidCount(long validCount) {
        this.validCount = validCount;
    }

    public long getNotRegisteredCount() {
        return notRegisteredCount;
    }

    public void setNotRegisteredCount(long notRegisteredCount) {
        this.notRegisteredCount = notRegisteredCount;
    }
}
