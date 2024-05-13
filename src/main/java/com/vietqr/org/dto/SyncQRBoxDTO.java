package com.vietqr.org.dto;

public class SyncQRBoxDTO {
    private String certificate;
    private String boxId;

    public SyncQRBoxDTO() {
    }

    public SyncQRBoxDTO(String certificate, String boxId) {
        this.certificate = certificate;
        this.boxId = boxId;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }
}
