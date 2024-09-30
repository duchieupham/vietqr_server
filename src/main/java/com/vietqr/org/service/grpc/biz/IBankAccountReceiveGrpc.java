package com.vietqr.org.service.grpc.biz;

public interface IBankAccountReceiveGrpc {
    String getBankAccount();
    String getUserBankName();
    boolean getIsSync();
    String getBankTypeId();
    String getBankShortName();
}
