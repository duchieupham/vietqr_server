package com.vietqr.org.dto.qrfeed;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.lang.reflect.Field;

public class QrCreateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String qrName;
    private String qrDescription;
    private String value;
    private String pin;
    private String isPublic;
    private String style;
    private String theme;

    public QrCreateRequestDTO() {
        super();
    }

    public QrCreateRequestDTO(String userId, String qrName, String qrDescription, String value, String pin, String isPublic, String style, String theme) {
        this.userId = userId;
        this.qrName = qrName;
        this.qrDescription = qrDescription;
        this.value = value;
        this.pin = pin;
        this.isPublic = isPublic;
        this.style = style;
        this.theme = theme;
    }



    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isNull() {
        Field fields[] = this.getClass().getDeclaredFields();
        for (Field f : fields) {
            try {
                Object value = f.get(this);
                if (value == null) {
                    return true;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
