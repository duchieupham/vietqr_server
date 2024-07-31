package com.vietqr.org.service.mqtt;

public class AdditionalData {
    private String terminalCode;
    private String serviceCode;
    private String additionalData;
    private String amount;
    private long timestamp;


    public AdditionalData(String terminalCode, String serviceCode, String additionalData, String amount, long timestamp) {
        this.terminalCode = terminalCode;
        this.serviceCode = serviceCode;
        this.additionalData = additionalData;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
