package com.vietqr.org.dto;

public class RequestActiveBankQrResponseDTO {
    private String otp;
    private int duration;
    private long validFrom;
    private long validTo;
    private String request;
    private String otpPayment;
    private String feeId;

    public RequestActiveBankQrResponseDTO() {
    }

    public RequestActiveBankQrResponseDTO(String otp, int duration, long validFrom, long validTo, String request) {
        this.otp = otp;
        this.duration = duration;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.request = request;
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

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getOtpPayment() {
        return otpPayment;
    }

    public void setOtpPayment(String otpPayment) {
        this.otpPayment = otpPayment;
    }

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }
}
