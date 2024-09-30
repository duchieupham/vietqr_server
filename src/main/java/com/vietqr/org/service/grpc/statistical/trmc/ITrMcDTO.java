package com.vietqr.org.service.grpc.statistical.trmc;

public interface ITrMcDTO {
    String getMerchantName();
    int getTotalNumberCredits();
    long getTotalAmountCredits();
    int getTotalReconTransactions();
    long getTotalAmountRecon();
}
