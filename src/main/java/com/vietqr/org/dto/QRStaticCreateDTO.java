package com.vietqr.org.dto;

public class QRStaticCreateDTO {
    private String qrCode;
    private String traceTransfer;

    public QRStaticCreateDTO() {
        super();
    }

    public QRStaticCreateDTO(String qrCode, String traceTransfer) {
        this.qrCode = qrCode;
        this.traceTransfer = traceTransfer;
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
}
