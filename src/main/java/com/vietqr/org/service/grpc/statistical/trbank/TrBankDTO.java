package com.vietqr.org.service.grpc.statistical.trbank;

public class TrBankDTO {
    private String bankShortName;
    private long totalAmountCredits;
    private long totalAmountRecon;
    private int totalNumberCredits;
    private int totalNumberRecon;

    public TrBankDTO() {
    }

    public TrBankDTO(ITrBankDTO dto) {
        this.bankShortName = dto.getBankShortName();
        this.totalAmountCredits = dto.getTotalAmountCredits();
        this.totalAmountRecon = dto.getTotalAmountRecon();
        this.totalNumberCredits = dto.getTotalNumberCredits();
        this.totalNumberRecon = dto.getTotalNumberRecon();
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public long getTotalAmountCredits() {
        return totalAmountCredits;
    }

    public void setTotalAmountCredits(long totalAmountCredits) {
        this.totalAmountCredits = totalAmountCredits;
    }

    public long getTotalAmountRecon() {
        return totalAmountRecon;
    }

    public void setTotalAmountRecon(long totalAmountRecon) {
        this.totalAmountRecon = totalAmountRecon;
    }

    public int getTotalNumberCredits() {
        return totalNumberCredits;
    }

    public void setTotalNumberCredits(int totalNumberCredits) {
        this.totalNumberCredits = totalNumberCredits;
    }

    public int getTotalNumberRecon() {
        return totalNumberRecon;
    }

    public void setTotalNumberRecon(int totalNumberRecon) {
        this.totalNumberRecon = totalNumberRecon;
    }
}
