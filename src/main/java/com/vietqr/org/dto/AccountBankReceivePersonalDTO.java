package com.vietqr.org.dto;

public interface AccountBankReceivePersonalDTO {

    String getBankId();

    String getBankAccount();

    String getUserBankName();

    String getBankName();

    String getBankCode();

    String getImgId();

    Integer getBankType();

    String getNationalId();

    String getPhoneAuthenticated();

    // for business bank
    String getBranchId();

    String getBusinessId();

    String getBranchName();

    String getBusinessName();

    String getBranchCode();

    String getBusinessCode();

    boolean getAuthenticated();

    String getUserId();
}
