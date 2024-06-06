package com.vietqr.org.dto;

public class FeePackageResponseDTO {
    private String bankId;
    private String month;
    private long totalCount;
    private String totalAmountFee;

    public FeePackageResponseDTO() {
    }

    public FeePackageResponseDTO(long totalCount, String totalAmountFee) {
        this.totalCount = totalCount;
        this.totalAmountFee = totalAmountFee;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public String getTotalAmountFee() {
        return totalAmountFee;
    }

    public void setTotalAmountFee(String totalAmountFee) {
        this.totalAmountFee = totalAmountFee;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
