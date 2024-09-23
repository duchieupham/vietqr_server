package com.vietqr.org.service.grpc.statistical.userregister;

public interface IUserRegisterDTO {
    String getUserCount();
    long getAndroidPlatform();
    long getIosPlatform();
    long getWebPlatform();
}
