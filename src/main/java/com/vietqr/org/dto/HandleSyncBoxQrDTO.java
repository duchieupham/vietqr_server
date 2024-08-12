package com.vietqr.org.dto;

public class HandleSyncBoxQrDTO {
    private String macAddr;
    private String checkSum;

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public HandleSyncBoxQrDTO(String macAddr, String checkSum) {
        this.macAddr = macAddr;
        this.checkSum = checkSum;
    }

    public HandleSyncBoxQrDTO() {
    }
}
