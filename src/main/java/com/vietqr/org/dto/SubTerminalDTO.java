package com.vietqr.org.dto;

public class SubTerminalDTO {
    private String subTerminalId;
    private String subTerminalCode;
    private String subRawTerminalCode;
    private String qrCode;
    private String traceTransfer;
    private String bankId;
    private String subTerminalName;
    private String subTerminalAddress;
    private int ratePrevDate;
    private int totalTrans;
    private long totalAmount;

    public SubTerminalDTO() {
    }

    public SubTerminalDTO(String subTerminalId, String subTerminalCode,
                          String subRawTerminalCode, String qrCode,
                          String traceTransfer, String bankId) {
        this.subTerminalId = subTerminalId;
        this.subTerminalCode = subTerminalCode;
        this.subRawTerminalCode = subRawTerminalCode;
        this.qrCode = qrCode;
        this.traceTransfer = traceTransfer;
        this.bankId = bankId;
    }

    public int getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(int totalTrans) {
        this.totalTrans = totalTrans;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSubTerminalName() {
        return subTerminalName;
    }

    public void setSubTerminalName(String subTerminalName) {
        this.subTerminalName = subTerminalName;
    }

    public String getSubTerminalAddress() {
        return subTerminalAddress;
    }

    public void setSubTerminalAddress(String subTerminalAddress) {
        this.subTerminalAddress = subTerminalAddress;
    }

    public String getSubTerminalId() {
        return subTerminalId;
    }

    public void setSubTerminalId(String subTerminalId) {
        this.subTerminalId = subTerminalId;
    }

    public String getSubTerminalCode() {
        return subTerminalCode;
    }

    public void setSubTerminalCode(String subTerminalCode) {
        this.subTerminalCode = subTerminalCode;
    }

    public String getSubRawTerminalCode() {
        return subRawTerminalCode;
    }

    public void setSubRawTerminalCode(String subRawTerminalCode) {
        this.subRawTerminalCode = subRawTerminalCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getTraceTransfer() {
        return traceTransfer;
    }

    public void setTraceTransfer(String traceTransfer) {
        this.traceTransfer = traceTransfer;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public int getRatePrevDate() {
        return ratePrevDate;
    }

    public void setRatePrevDate(int ratePrevDate) {
        this.ratePrevDate = ratePrevDate;
    }
}
