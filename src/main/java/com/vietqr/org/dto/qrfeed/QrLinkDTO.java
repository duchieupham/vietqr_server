package com.vietqr.org.dto.qrfeed;

import java.io.Serializable;

public class QrLinkDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String qrName;
    private String qrDescription;
    private String urlLink;

    public QrLinkDTO() {
    }

    public QrLinkDTO(String qrName, String qrDescription, String urlLink) {
        this.qrName = qrName;
        this.qrDescription = qrDescription;
        this.urlLink = urlLink;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }

    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public String getQrDescription() {
        return qrDescription;
    }

    public void setQrDescription(String qrDescription) {
        this.qrDescription = qrDescription;
    }

    public String getQrValue() {
        return urlLink;
    }

    public void setQrValue(String qrValue) {
        this.urlLink = qrValue;
    }
}
