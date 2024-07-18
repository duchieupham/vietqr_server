package com.vietqr.org.dto.qrfeed;

import java.io.Serializable;

public class QrWalletResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String qrName;
    private String value;
    private String qrContent;
    private String imgId;
    private int existing;
    private String publicRefId;
    private String qrLink;

    public QrWalletResponseDTO() {
        super();
    }

    public QrWalletResponseDTO(String qrName, String value, String qrContent, String imgId, int existing, String publicRefId, String qrLink) {
        this.qrName = qrName;
        this.value = value;
        this.qrContent = qrContent;
        this.imgId = imgId;
        this.existing = existing;
        this.publicRefId = publicRefId;
        this.qrLink = qrLink;
    }


    public String getQrName() {
        return qrName;
    }

    public void setQrName(String qrName) {
        this.qrName = qrName;
    }

    public String getQrContent() {
        return qrContent;
    }

    public void setQrContent(String qrContent) {
        this.qrContent = qrContent;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public int getExisting() {
        return existing;
    }

    public void setExisting(int existing) {
        this.existing = existing;
    }

    public String getPublicRefId() {
        return publicRefId;
    }

    public void setPublicRefId(String publicRefId) {
        this.publicRefId = publicRefId;
    }

    public String getQrLink() {
        return qrLink;
    }

    public void setQrLink(String qrLink) {
        this.qrLink = qrLink;
    }
}
