package com.vietqr.org.dto;

public interface ITransStatisticResponseWebDTO {
    int getTotalTrans();
    long getTotalCashIn();
    int getTotalSettled();
    long getTotalCashSettled();
    int getTotalUnsettled();
    long getTotalCashUnsettled();
}
