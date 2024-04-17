package com.vietqr.org.dto;

public interface AccountBankSmsDetailDTO {
    // id, bankAccount, userBankName, bankTypeId, smsId, status, type,
    // bankShortName, bankCode, bankName, imgId

    String getId();

    String getUserBankName();

    String getBankTypeId();

    String getSmsId();

    Boolean getStatus();

    Integer getType();

    String getBankShortName();

    String getBankCode();

    String getBankName();

    String getImgId();

}
