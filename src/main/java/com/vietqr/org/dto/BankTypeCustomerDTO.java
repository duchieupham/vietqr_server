package com.vietqr.org.dto;

import java.io.Serializable;

public class BankTypeCustomerDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String bankCode;
    private String bankName;
    private String shortName;
    private String imgUrl;
    private String bin;
    private String swiftCode;

    public BankTypeCustomerDTO() {
        super();
    }

    public BankTypeCustomerDTO(String bankCode, String bankName, String shortName, String imgUrl, String bin,
            String swiftCode) {
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.shortName = shortName;
        this.imgUrl = imgUrl;
        this.bin = bin;
        this.swiftCode = swiftCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }

}
