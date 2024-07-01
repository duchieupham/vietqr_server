package com.vietqr.org.dto.qrfeed;

import com.vietqr.org.dto.VietQRCreateUnauthenticatedDTO;

import java.lang.reflect.Field;

public class VietQRCreateUnauthenticatedExtendDTO extends VietQRCreateUnauthenticatedDTO {
    private String userId;
    private int style;
    private int theme;

    public VietQRCreateUnauthenticatedExtendDTO() {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
