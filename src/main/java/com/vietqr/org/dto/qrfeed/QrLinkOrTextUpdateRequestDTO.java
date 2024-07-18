package com.vietqr.org.dto.qrfeed;

import java.io.Serializable;

public class QrLinkOrTextUpdateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String qrId;
    private String title;
    private String qrDescription;
    private String value;
    private String pin;
    private int isPublic;
    private int style;
    private int theme;

    public QrLinkOrTextUpdateRequestDTO() {
        super();
    }

    public QrLinkOrTextUpdateRequestDTO(String userId, String qrId, String title, String qrDescription, String value, String pin, int isPublic, int style, int theme) {
        this.userId = userId;
        this.qrId = qrId;
        this.title = title;
        this.qrDescription = qrDescription;
        this.value = value;
        this.pin = pin;
        this.isPublic = isPublic;
        this.style = style;
        this.theme = theme;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
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


}
