package com.vietqr.org.dto;

public class TransactionFeePackageResponseDTO {
    private String timeProcess;
    private String bankAccount;
    private String bankShortName;
    private String connectionType;
    private String title;
    private int totalCount;
    private long totalAmountReceive;
    private long fixFee;
    private double percentFee;
    private long vatAmount;
    private long totalAmount;
    private double vat;
    private long totalAfterVat;

    public TransactionFeePackageResponseDTO() {
    }

    public TransactionFeePackageResponseDTO(String timeProcess, String bankAccount, String bankShortName, String connectionType,
                                            String title, int totalCount, long totalAmountReceive, long fixFee, double percentFee,
                                            long vatAmount, long totalAmount, double vat, long totalAfterVat) {
        this.timeProcess = timeProcess;
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.connectionType = connectionType;
        this.title = title;
        this.totalCount = totalCount;
        this.totalAmountReceive = totalAmountReceive;
        this.fixFee = fixFee;
        this.percentFee = percentFee;
        this.vatAmount = vatAmount;
        this.totalAmount = totalAmount;
        this.vat = vat;
        this.totalAfterVat = totalAfterVat;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setPercentFee(double percentFee) {
        this.percentFee = percentFee;
    }

    public long getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(long vatAmount) {
        this.vatAmount = vatAmount;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public String getTimeProcess() {
        return timeProcess;
    }

    public void setTimeProcess(String timeProcess) {
        this.timeProcess = timeProcess;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
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

    public long getFixFee() {
        return fixFee;
    }

    public void setFixFee(long fixFee) {
        this.fixFee = fixFee;
    }


    public void setPercentFee(long percentFee) {
        this.percentFee = percentFee;
    }

    public long getTotalAmountReceive() {
        return totalAmountReceive;
    }

    public void setTotalAmountReceive(long totalAmountReceive) {
        this.totalAmountReceive = totalAmountReceive;
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

    public double getPercentFee() {
        return percentFee;
    }

    public double getVat() {
        return vat;
    }
}
