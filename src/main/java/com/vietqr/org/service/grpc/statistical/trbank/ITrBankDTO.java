package com.vietqr.org.service.grpc.statistical.trbank;

public interface ITrBankDTO {
    String getBankShortName();
    long getTotalAmountCredits();
    long getTotalAmountRecon();
    int getTotalNumberCredits();
    int getTotalNumberRecon();
}
