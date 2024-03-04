package com.vietqr.org.dto;

public interface ITerminalWebResponseDTO {
    String getId();

    String getName();

    String getAddress();

    String getCode();

    int getTotalTrans();

    long getTotalAmount();
}
