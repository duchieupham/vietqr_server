package com.vietqr.org.service.grpc.statistical;

public interface ITrSysDTO {
    int getTotalNumberCredits();
    Long getTotalAmountCredits() ;
    int getTotalNumberRecon();
    Long getTotalAmountRecon();
    int getTotalNumberWithoutRecon();
    Long getTotalAmountWithoutRecon();
    int getTotalNumberPushError();
    Long getTotalAmountPushErrorSum();
}
