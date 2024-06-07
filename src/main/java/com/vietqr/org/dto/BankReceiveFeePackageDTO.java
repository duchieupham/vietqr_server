package com.vietqr.org.dto;

public class BankReceiveFeePackageDTO {
    private String bankAccount ;
    private String bankShortName;
    private String userBankName;
    private long fixFee;
    private int recordType;
    private double vat;
    private String title;
    private boolean mmsActive;
    private double percentFee;
    public BankReceiveFeePackageDTO(){
        this.bankAccount ="";
        this.bankShortName ="";
        this.userBankName = "";
        this.fixFee = 0;
        this.vat =0.0;
        this.mmsActive = false;
        this.percentFee = 0.0;
    }

    public BankReceiveFeePackageDTO(String bankAccount, String bankShortName, String userBankName, long fixFee,
                                    int recordType, double vat, String title, boolean mmsActive, double percentFee) {
        this.bankAccount = bankAccount;
        this.bankShortName = bankShortName;
        this.userBankName = userBankName;
        this.fixFee = fixFee;
        this.recordType = recordType;
        this.vat = vat;
        this.title = title;
        this.mmsActive = mmsActive;
        this.percentFee = percentFee;
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

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public long getFixFee() {
        return fixFee;
    }

    public void setFixFee(long fixFee) {
        this.fixFee = fixFee;
    }

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isMmsActive() {
        return mmsActive;
    }

    public void setMmsActive(boolean mmsActive) {
        this.mmsActive = mmsActive;
    }

    public double getPercentFee() {
        return percentFee;
    }

    public void setPercentFee(double percentFee) {
        this.percentFee = percentFee;
    }
}
