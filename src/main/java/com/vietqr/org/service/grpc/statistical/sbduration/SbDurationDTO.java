package com.vietqr.org.service.grpc.statistical.sbduration;

public class SbDurationDTO {
    private int overdueCount;
    private int nearlyExpireCount;

    public SbDurationDTO() {
    }

    public SbDurationDTO(ISbDurationDTO dto) {
        this.overdueCount = dto.getOverdueCount();
        this.nearlyExpireCount = dto.getNearlyExpireCount();
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    public void setOverdueCount(int overdueCount) {
        this.overdueCount = overdueCount;
    }

    public int getNearlyExpireCount() {
        return nearlyExpireCount;
    }

    public void setNearlyExpireCount(int nearlyExpireCount) {
        this.nearlyExpireCount = nearlyExpireCount;
    }
}
