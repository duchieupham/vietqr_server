package com.vietqr.org.service.mqtt;

public class AdditionalDataInTransaction {
    private String amount;
    private Long dateTimeQr;
    private String serviceCode;
    private String terminalCode;
    private String additionalData1;

    public AdditionalDataInTransaction(String amount, Long dateTimeQr, String serviceCode, String terminalCode, String additionalData1) {
        this.amount = amount;
        this.dateTimeQr = dateTimeQr;
        this.serviceCode = serviceCode;
        this.terminalCode = terminalCode;
        this.additionalData1 = additionalData1;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Long getDateTimeQr() {
        return dateTimeQr;
    }

    public void setDateTimeQr(Long dateTimeQr) {
        this.dateTimeQr = dateTimeQr;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getAdditionalData1() {
        return additionalData1;
    }

    public void setAdditionalData1(String additionalData1) {
        this.additionalData1 = additionalData1;
    }
}
