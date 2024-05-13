package com.vietqr.org.dto;

public class SyncBoxQrDTO {
    private String qrCertificate;
    private String boxId;

    public SyncBoxQrDTO() {
    }

    public SyncBoxQrDTO(String qrCertificate, String boxId) {
        this.qrCertificate = qrCertificate;
        this.boxId = boxId;
    }

    public String getQrCertificate() {
        return qrCertificate;
    }

    public void setQrCertificate(String qrCertificate) {
        this.qrCertificate = qrCertificate;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }
}
