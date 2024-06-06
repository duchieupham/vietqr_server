package com.vietqr.org.dto;

public class TransactionFeePagekageResponseDTO {
    private String timeProcess;
    private String accountBank;
    private String bankName;
    private int mmsActive;
    private String title;
    private long totalCount;
    private long totalAmountReceive;
    private long fixFee;
    private long percentFee;
    private long amount;
    private long totalAmount;
    private long vat;
    private long totalAfterVat;

    public TransactionFeePagekageResponseDTO() {
    }

    public TransactionFeePagekageResponseDTO(String timeProcess, String accountBank, String bankName, int mmsActive, String title, long totalCount, long totalAmountReceive, long fixFee, long percentFee, long amount, long totalAmount, long vat, long totalAfterVat) {
        this.timeProcess = timeProcess;
        this.accountBank = accountBank;
        this.bankName = bankName;
        this.mmsActive = mmsActive;
        this.title = title;
        this.totalCount = totalCount;
        this.totalAmountReceive = totalAmountReceive;
        this.fixFee = fixFee;
        this.percentFee = percentFee;
        this.amount = amount;
        this.totalAmount = totalAmount;
        this.vat = vat;
        this.totalAfterVat = totalAfterVat;
    }

    public String getTimeProcess() {
        return timeProcess;
    }

    public void setTimeProcess(String timeProcess) {
        this.timeProcess = timeProcess;
    }

    public String getAccountBank() {
        return accountBank;
    }

    public void setAccountBank(String accountBank) {
        this.accountBank = accountBank;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getMmsActive() {
        return mmsActive;
    }

    public void setMmsActive(int mmsActive) {
        this.mmsActive = mmsActive;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getTotalAmountReceive() {
        return totalAmountReceive;
    }

    public void setTotalAmountReceive(long totalAmountReceive) {
        this.totalAmountReceive = totalAmountReceive;
    }

    public long getFixFee() {
        return fixFee;
    }

    public void setFixFee(long fixFee) {
        this.fixFee = fixFee;
    }

    public long getPercentFee() {
        return percentFee;
    }

    public void setPercentFee(long percentFee) {
        this.percentFee = percentFee;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getVat() {
        return vat;
    }

    public void setVat(long vat) {
        this.vat = vat;
    }

    public long getTotalAfterVat() {
        return totalAfterVat;
    }

    public void setTotalAfterVat(long totalAfterVat) {
        this.totalAfterVat = totalAfterVat;
    }
}
