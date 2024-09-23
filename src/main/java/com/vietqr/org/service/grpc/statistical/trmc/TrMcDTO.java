package com.vietqr.org.service.grpc.statistical.trmc;

public class TrMcDTO {
    private String merchantName;
    private int totalNumberCredits;
    private long totalAmountCredits;
    private int totalReconTransactions;
    private long totalAmountRecon;

    public TrMcDTO() {
    }

    public TrMcDTO(ITrMcDTO dto) {
        this.merchantName = dto.getMerchantName();
        this.totalNumberCredits = dto.getTotalNumberCredits();
        this.totalAmountCredits = dto.getTotalAmountCredits();
        this.totalReconTransactions = dto.getTotalReconTransactions();
        this.totalAmountRecon = dto.getTotalAmountRecon();
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public int getTotalNumberCredits() {
        return totalNumberCredits;
    }

    public void setTotalNumberCredits(int totalNumberCredits) {
        this.totalNumberCredits = totalNumberCredits;
    }

    public long getTotalAmountCredits() {
        return totalAmountCredits;
    }

    public void setTotalAmountCredits(long totalAmountCredits) {
        this.totalAmountCredits = totalAmountCredits;
    }

    public int getTotalReconTransactions() {
        return totalReconTransactions;
    }

    public void setTotalReconTransactions(int totalReconTransactions) {
        this.totalReconTransactions = totalReconTransactions;
    }

    public long getTotalAmountRecon() {
        return totalAmountRecon;
    }

    public void setTotalAmountRecon(long totalAmountRecon) {
        this.totalAmountRecon = totalAmountRecon;
    }
}
