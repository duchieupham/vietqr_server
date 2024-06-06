package com.vietqr.org.dto;

public class TransactionFeePagekageResponseDTO {
    private String timeProcess;
    private String accountBank;
    private String bankName;
    private String mmsActive;
    private String title;
    private String totalCount;
    private String totalAmountReceive;
    private String fixFee;
    private String percentFee;
    private String amount;
    private String totalAmount;
    private String vat;
    private String totalAfterVat;

    public TransactionFeePagekageResponseDTO(String timeProcess, String accountBank, String bankName, String mmsActive, String title, String totalCount, String totalAmountReceive, String fixFee, String percentFee, String amount, String totalAmount, String vat, String totalAfterVat) {
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

    public TransactionFeePagekageResponseDTO() {
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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

    public String getMmsActive() {
        return mmsActive;
    }

    public void setMmsActive(String mmsActive) {
        this.mmsActive = mmsActive;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getTotalAmountReceive() {
        return totalAmountReceive;
    }

    public void setTotalAmountReceive(String totalAmountReceive) {
        this.totalAmountReceive = totalAmountReceive;
    }

    public String getFixFee() {
        return fixFee;
    }

    public void setFixFee(String fixFee) {
        this.fixFee = fixFee;
    }

    public String getPercentFee() {
        return percentFee;
    }

    public void setPercentFee(String percentFee) {
        this.percentFee = percentFee;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getTotalAfterVat() {
        return totalAfterVat;
    }

    public void setTotalAfterVat(String totalAfterVat) {
        this.totalAfterVat = totalAfterVat;
    }
}
