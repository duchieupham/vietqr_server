package com.vietqr.org.dto;

import java.io.Serializable;

public class AccountBankShareResponseDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String bankAccount;
    private String userBankName;
    private String bankShortName;
    private String bankCode;
    private String bankName;
    private String imgId;
    private int type;
    private String bankTypeId;
    private boolean isAuthenticated;
    private String nationalId;
    private String phoneAuthenticated;
    private String userId;
    private boolean isOwner;
    private int bankTypeStatus;
    private String qrCode;
    private String caiValue;
    private String ewalletToken;
    private Integer unlinkedType;

    public AccountBankShareResponseDTO() {
        super();
    }


    public AccountBankShareResponseDTO(String id, String bankAccount, String userBankName,
                                       String bankShortName, String bankCode, String bankName,
                                       String imgId, int type, String bankTypeId, boolean isAuthenticated,
                                       String nationalId, String phoneAuthenticated, String userId,
                                       boolean isOwner, int bankTypeStatus, String qrCode, String caiValue,
                                       String ewalletToken, Integer unlinkedType) {
        this.id = id;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.bankShortName = bankShortName;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.imgId = imgId;
        this.type = type;
        this.bankTypeId = bankTypeId;
        this.isAuthenticated = isAuthenticated;
        this.nationalId = nationalId;
        this.phoneAuthenticated = phoneAuthenticated;
        this.userId = userId;
        this.isOwner = isOwner;
        this.bankTypeStatus = bankTypeStatus;
        this.qrCode = qrCode;
        this.caiValue = caiValue;
        this.ewalletToken = ewalletToken;
        this.unlinkedType = unlinkedType;
    }

    public String getCaiValue() {
        return caiValue;
    }

    public void setCaiValue(String caiValue) {
        this.caiValue = caiValue;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getBankShortName() {
        return bankShortName;
    }

    public void setBankShortName(String bankShortName) {
        this.bankShortName = bankShortName;
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

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean owner) {
        this.isOwner = owner;
    }

    public int getBankTypeStatus() {
        return bankTypeStatus;
    }

    public void setBankTypeStatus(int bankTypeStatus) {
        this.bankTypeStatus = bankTypeStatus;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getBankTypeId() {
        return bankTypeId;
    }

    public void setBankTypeId(String bankTypeId) {
        this.bankTypeId = bankTypeId;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getEwalletToken() {
        return ewalletToken;
    }

    public void setEwalletToken(String ewalletToken) {
        this.ewalletToken = ewalletToken;
    }

    public Integer getUnlinkedType() {
        return unlinkedType;
    }

    public void setUnlinkedType(Integer unlinkedType) {
        this.unlinkedType = unlinkedType;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
