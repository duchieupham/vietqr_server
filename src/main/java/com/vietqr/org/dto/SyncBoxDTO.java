package com.vietqr.org.dto;

public class SyncBoxDTO {
    private String macAddr;

    public SyncBoxDTO() {
    }

    public SyncBoxDTO(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }
}
