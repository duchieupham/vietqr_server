package com.vietqr.org.dto;

public interface AccountBankReceiveShareDTO {

    String getBankId();

    String getBankAccount();

    String getUserBankName();

    String getImgId();

    Integer getBankType();

    String getNationalId();

    String getPhoneAuthenticated();

    boolean getAuthenticated();

    String getUserId();

    boolean getIsOwner();

    String getBankTypeId();

    Boolean getIsValidService();

    Long getValidFeeFrom();

    Long getValidFeeTo();
}
