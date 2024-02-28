package com.vietqr.org.entity.redis;

public class SumEachBankEntity {
    private long numberOfBankAuthenticated;
    private long numberOfBankUnauthenticated;
    private long numberOfBank;
    private String bankName;
    private String bankCode;
    private String bankTypeId;
    private String bankShortName;
    private String imgId;
    public SumEachBankEntity() {
    }

    public long getNumberOfBankAuthenticated() {
        return numberOfBankAuthenticated;
    }

    public void setNumberOfBankAuthenticated(long numberOfBankAuthenticated) {
        this.numberOfBankAuthenticated = numberOfBankAuthenticated;
    }

    public long getNumberOfBankUnauthenticated() {
        return numberOfBankUnauthenticated;
    }

    public void setNumberOfBankUnauthenticated(long numberOfBankUnauthenticated) {
        this.numberOfBankUnauthenticated = numberOfBankUnauthenticated;
    }

    public long getNumberOfBank() {
        return numberOfBank;
    }

    public void setNumberOfBank(long numberOfBank) {
        this.numberOfBank = numberOfBank;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankTypeId() {
        return bankTypeId;
    }

    public void setBankTypeId(String bankTypeId) {
        this.bankTypeId = bankTypeId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }
}
