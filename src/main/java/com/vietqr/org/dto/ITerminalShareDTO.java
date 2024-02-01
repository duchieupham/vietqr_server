package com.vietqr.org.dto;

public interface ITerminalShareDTO {
    String getTerminalId();

    String getTerminalName();

    int getTotalMembers();

    String getTerminalCode();

    String getTerminalAddress();

    boolean getIsDefault();

    String getBankId();
}
