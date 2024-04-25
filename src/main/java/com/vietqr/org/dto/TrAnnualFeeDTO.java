package com.vietqr.org.dto;

public class TrAnnualFeeDTO {
    private String feeId;
    private int duration;
    private int amount;
    private String description;
    private long totalAmount;
    private double vat;
    private long totalWithVat;

    public TrAnnualFeeDTO() {
    }

    public TrAnnualFeeDTO(String feeId, int duration, int amount, String description,
                          long totalAmount, double vat, long totalWithVat) {
        this.feeId = feeId;
        this.duration = duration;
        this.amount = amount;
        this.description = description;
        this.totalAmount = totalAmount;
        this.vat = vat;
        this.totalWithVat = totalWithVat;
    }

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public long getTotalWithVat() {
        return totalWithVat;
    }

    public void setTotalWithVat(long totalWithVat) {
        this.totalWithVat = totalWithVat;
    }
}
