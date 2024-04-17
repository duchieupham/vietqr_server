package com.vietqr.org.dto;

public class RequestActiveBankResponseDTO {
    private String otp;
    private int duration;
    private long validFrom;
    private long validTo;
    private String key;

    public RequestActiveBankResponseDTO() {
    }

    public RequestActiveBankResponseDTO(String otp, int duration, long validFrom, long validTo, String key) {
        this.otp = otp;
        this.duration = duration;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.key = key;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(long validFrom) {
        this.validFrom = validFrom;
    }

    public long getValidTo() {
        return validTo;
    }

    public void setValidTo(long validTo) {
        this.validTo = validTo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
