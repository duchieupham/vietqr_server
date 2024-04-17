package com.vietqr.org.dto;

public interface ITerminalDetailWebDTO {
    String getTerminalId();
    String getTerminalName();
    String getTerminalAddress();
    int getTotalTrans();
    long getTotalAmount();
    int getTotalMember();
    String getTerminalCode();
    String getBankName();
    String getBankAccount();
    String getBankShortName();
    String getBankAccountName();
}
