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
    private String ewalletToken;
    private Integer unlinkedType;
    private Boolean isActiveService;
    private Long validFeeFrom;
    private Long validFeeTo;
    private int transCount;
//    private List<BusinessBankDetailDTO> businessDetails;

    public AccountBankReceiveDetailWT() {
        super();
    }

    public AccountBankReceiveDetailWT(String id, String bankAccount, String userBankName, String bankCode,
            String bankName, String imgId, String bankTypeId, int bankTypeStatus, String userId, int type,
            boolean isAuthenticated, String nationalId,
            String phoneAuthenticated, String qrCode,
            String ewalletToken, Integer unlinkedType,
            List<BusinessBankDetailDTO> businessDetails) {
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
        this.ewalletToken = ewalletToken;
        this.unlinkedType = unlinkedType;
//        this.businessDetails = businessDetails;

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

    public Boolean getIsActiveService() {
        return isActiveService;
    }

    public void setIsActiveService(Boolean activeService) {
        isActiveService = activeService;
    }

    public Long getValidFeeFrom() {
        return validFeeFrom;
    }

    public void setValidFeeFrom(Long validFeeFrom) {
        this.validFeeFrom = validFeeFrom;
    }

    public Long getValidFeeTo() {
        return validFeeTo;
    }

    public void setValidFeeTo(Long validFeeTo) {
        this.validFeeTo = validFeeTo;
    }

    public int getTransCount() {
        return transCount;
    }

    public void setTransCount(int transCount) {
        this.transCount = transCount;
    }

}