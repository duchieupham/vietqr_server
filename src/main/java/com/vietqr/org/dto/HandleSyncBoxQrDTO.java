package com.vietqr.org.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HandleSyncBoxQrDTO {
    @JsonProperty(value = "macAddr")
    private String macAddr;

    @JsonProperty(value = "serialNumber")
    private String serialNumber;

    @JsonProperty(value = "secretKey")
    private String secretKey;

    @JsonProperty(value = "checkSum")
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public HandleSyncBoxQrDTO(String macAddr, String checkSum) {
        this.macAddr = macAddr;
        this.checkSum = checkSum;
    }

    public HandleSyncBoxQrDTO() {
    }
}
