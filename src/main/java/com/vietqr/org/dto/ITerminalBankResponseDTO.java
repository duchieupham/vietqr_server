package com.vietqr.org.dto;

public interface ITerminalBankResponseDTO {
    String getTerminalId();
    String getBankId();
    String getBankName();
    String getBankCode();
    boolean getIsMmsActive();
    String getBankAccount();
    String getUserBankName();
    String getBankShortName();
    String getImgId();
    String getQrCode();
}
