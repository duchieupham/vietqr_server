package com.vietqr.org.dto.qrfeed;

import java.io.Serializable;

public class QrLinkOrTextUpdateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String qrDescription;
    private String text;
    private String pin;
    private int isPublic;

    public QrLinkOrTextUpdateRequestDTO() {
        super();
    }

    public QrLinkOrTextUpdateRequestDTO(String title, String qrDescription, String text, String pin, int isPublic) {
        this.title = title;
        this.qrDescription = qrDescription;
        this.text = text;
        this.pin = pin;
        this.isPublic = isPublic;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQrDescription() {
        return qrDescription;
    }

    public void setQrDescription(String qrDescription) {
        this.qrDescription = qrDescription;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
