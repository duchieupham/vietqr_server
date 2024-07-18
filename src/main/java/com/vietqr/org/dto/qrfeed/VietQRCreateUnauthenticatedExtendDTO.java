package com.vietqr.org.dto.qrfeed;

import com.vietqr.org.dto.VietQRCreateUnauthenticatedDTO;

import java.lang.reflect.Field;

public class VietQRCreateUnauthenticatedExtendDTO extends VietQRCreateUnauthenticatedDTO {
    private String id;
    private String userId;
    private String qrName;
    private String qrDescription;
    private String isPublic;
    private String style;
    private String theme;

    public VietQRCreateUnauthenticatedExtendDTO() {
    }

    public VietQRCreateUnauthenticatedExtendDTO(String qrId, String userId, String qrName, String qrDescription, String isPublic, String style, String theme) {
        this.id = qrId;
        this.userId = userId;
        this.qrName = qrName;
        this.qrDescription = qrDescription;
        this.isPublic = isPublic;
        this.style = style;
        this.theme = theme;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
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

    public boolean isNull() {
        Field fields[] = this.getClass().getDeclaredFields();
        for (Field f : fields) {
            try {
                Object value = f.get(this);
                if (value == null) {
                    return true;
                }
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
