package com.vietqr.org.dto;

public class MasterDataDTO {
    private String mid;
    private String midName;
    private String certificate;
    private String webhook;
    private String webSocket;

    public MasterDataDTO() {
    }

    public MasterDataDTO(String mid, String midName, String certificate, String webhook, String webSocket) {
        this.mid = mid;
        this.midName = midName;
        this.certificate = certificate;
        this.webhook = webhook;
        this.webSocket = webSocket;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMidName() {
        return midName;
    }

    public void setMidName(String midName) {
        this.midName = midName;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(String webSocket) {
        this.webSocket = webSocket;
    }
}
