package com.vietqr.org.dto;

public interface KeyActiveBankReceiveDTO {
    String getKeyActive();
    int getStatus();
    int getDuration();
    String getValueActive();
    String getSecretKey();
    long getCreateAt();
    int getVersion();
}
