package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

import com.vietqr.org.dto.AccountBankReceiveDetailDTO.BusinessBankDetailDTO;

public class AccountBankReceiveDetailWT implements Serializable {

    /**
    *
    */
    private static final long serialVersionUID = 1L;

    private String id;
    private String bankAccount;
    private String userBankName;
    private String bankCode;
    private String bankName;
    private String imgId;
    private String bankTypeId;
    private int bankTypeStatus;
    // userId who create bank account
    private String userId;
    private int type;
    private boolean isAuthenticated;
    private String nationalId;
    private String qrCode;
    private String phoneAuthenticated;
    private List<BusinessBankDetailDTO> businessDetails;

    public AccountBankReceiveDetailWT() {
        super();
    }

    public AccountBankReceiveDetailWT(String id, String bankAccount, String userBankName, String bankCode,
            String bankName, String imgId, String bankTypeId, int bankTypeStatus, String userId, int type,
            boolean isAuthenticated, String nationalId,
            String phoneAuthenticated, String qrCode, List<BusinessBankDetailDTO> businessDetails) {
        this.id = id;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.imgId = imgId;
        this.type = type;
        this.bankTypeId = bankTypeId;
        this.bankTypeStatus = bankTypeStatus;
        this.userId = userId;
        this.isAuthenticated = isAuthenticated;
        this.nationalId = nationalId;
        this.qrCode = qrCode;
        this.phoneAuthenticated = phoneAuthenticated;
        this.businessDetails = businessDetails;

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

    public String getBankTypeId() {
        return bankTypeId;
    }

    public void setBankTypeId(String bankTypeId) {
        this.bankTypeId = bankTypeId;
    }

    public int getBankTypeStatus() {
        return bankTypeStatus;
    }

    public void setBankTypeStatus(int bankTypeStatus) {
        this.bankTypeStatus = bankTypeStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public List<BusinessBankDetailDTO> getBusinessDetails() {
        return businessDetails;
    }

    public void setBusinessDetails(List<BusinessBankDetailDTO> businessDetails) {
        this.businessDetails = businessDetails;
    }

}