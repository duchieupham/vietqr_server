package com.vietqr.org.dto;

public class StatisticMerchantDTO {
    private String merchantId;
    private String merchantName;
    private String vsoCode;
    private String date;
    private int totalTrans;
    private long totalAmount;
    private int totalTerminal;
    private int ratePreviousDay;
    private int ratePreviousMonth;

    public StatisticMerchantDTO() {
    }

    public StatisticMerchantDTO(String merchantId, String merchantName, String vsoCode, String date, int totalTrans, long totalAmount, int totalTerminal, int ratePreviousDay, int ratePreviousMonth) {
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.vsoCode = vsoCode;
        this.date = date;
        this.totalTrans = totalTrans;
        this.totalAmount = totalAmount;
        this.totalTerminal = totalTerminal;
        this.ratePreviousDay = ratePreviousDay;
        this.ratePreviousMonth = ratePreviousMonth;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getVsoCode() {
        return vsoCode;
    }

    public void setVsoCode(String vsoCode) {
        this.vsoCode = vsoCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(int totalTrans) {
        this.totalTrans = totalTrans;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalTerminal() {
        return totalTerminal;
    }

    public void setTotalTerminal(int totalTerminal) {
        this.totalTerminal = totalTerminal;
    }

    public int getRatePreviousDay() {
        return ratePreviousDay;
    }

    public void setRatePreviousDay(int ratePreviousDay) {
        this.ratePreviousDay = ratePreviousDay;
    }

    public int getRatePreviousMonth() {
        return ratePreviousMonth;
    }

    public void setRatePreviousMonth(int ratePreviousMonth) {
        this.ratePreviousMonth = ratePreviousMonth;
    }
}
