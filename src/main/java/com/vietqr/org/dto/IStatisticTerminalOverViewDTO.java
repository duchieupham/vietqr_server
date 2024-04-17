package com.vietqr.org.dto;

public interface IStatisticTerminalOverViewDTO {
    String getTerminalId();

    String getTerminalCode();

    String getTerminalName();

    String getTerminalAddress();

    int getTotalTrans();

    long getTotalAmount();
}
